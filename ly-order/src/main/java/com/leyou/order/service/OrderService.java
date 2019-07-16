package com.leyou.order.service;

import com.github.wxpay.sdk.WXPay;
import com.leyou.client.ItemClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.threadLocals.UserHolder;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.dto.SkuDTO;
import com.leyou.order.dto.CartDTO;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.entity.Order;
import com.leyou.order.entity.OrderDetail;
import com.leyou.order.entity.OrderLogistics;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderLogisticsMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.utils.PayHelper;
import com.leyou.order.vo.OrderDetailVO;
import com.leyou.order.vo.OrderLogisticsVO;
import com.leyou.order.vo.OrderVO;
import com.leyou.user.UserClient;
import com.leyou.user.dto.AddressDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: dzw
 * @Date: 2019/7/3 10:53
 * @Version 1.0
 */
@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper detailMapper;
    @Autowired
    private OrderLogisticsMapper logisticsMapper;


    @Autowired
    private IdWorker idWorker;

    @Autowired
    private UserClient userClient;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Autowired
    private PayHelper payHelper;

    @Autowired
    private ItemClient itemClient;
    /**
     * 这是创建订单
     * @param orderDTO  发过来的订单
     * @return     返回订单编号
     */
    @Transactional
    public Long createOrder(OrderDTO orderDTO) {

        //1.组织Order数据，完成新增
        // 写order
        Order order = new Order();
        //- 订单编号  根据雪花算法生成
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);

        //- 用户id
        Long userId = UserHolder.getUser();
        order.setUserId(userId);
        //- 订单金额相关数据，需要查询商品信息后逐个运算并累加获取
        List<CartDTO> carts = orderDTO.getCarts();
        //skuIds 的列表
        List<Long> skuIds = carts.stream().map(CartDTO::getSkuId)
                .collect(Collectors.toList());
        //构建 key为 skuid value为 num的map 用以下单减库存
        Map<Long, Integer> map = carts.stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));

        List<SkuDTO> skuDTOS = itemClient.querySkuByIds(skuIds);
        //将这些sku的价格加在一起
        //2.组织OrderDetail数据，完成新增
        ArrayList<OrderDetail> orderDatail = new ArrayList<>();
        //- 订单状态数据

        long total = 0;
        for (SkuDTO skuDTO : skuDTOS) {
            total += (skuDTO.getPrice() * map.get(skuDTO.getId()));
            //组装ordertatail
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(orderId);
            detail.setSkuId(skuDTO.getId());
            detail.setImage(StringUtils.substringBefore(skuDTO.getImages(),","));
            detail.setNum(map.get(skuDTO.getId()));
            detail.setOwnSpec(skuDTO.getOwnSpec());
            detail.setPrice(skuDTO.getPrice());
            detail.setTitle(skuDTO.getTitle());
            orderDatail.add(detail);
        }
        //添加邮费 实付金额 快递费
        order.setPostFee(0l);
        order.setTotalFee(total);
        //查询促销活动 减多少
        //实付
        order.setActualFee(total);
        //订单状态
        order.setStatus(OrderStatusEnum.INIT.value());
        //将商品添加到数据库
        int count = orderMapper.insertSelective(order);
        if (count != 1) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        //将商品详情 添加到详情表里面
        count = detailMapper.insertDetailList(orderDatail);
        if (count != orderDatail.size()) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

        //3.组织OrderLogistics数据，完成新增
        //查询地址  去用户那里查
        AddressDTO addressDTO = userClient.queryAddressById(userId, orderDTO.getAddressId());
        //传递到OrderLogistics 相同的地方赋值
        OrderLogistics logistics = BeanHelper.copyProperties(addressDTO, OrderLogistics.class);
        logistics.setOrderId(orderId);
        count = logisticsMapper.insertSelective(logistics);
        if (count != orderDatail.size()) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        //4.下单成功后，商品对应库存应该减掉
        //在商品微服务里面删除
        itemClient.minusStock(map);
        return orderId;
    }

    /**
     * 根据订单id 查询订单的信息  其中包括 订单详情的信息 订单物流的信息 和订单信息
     * @param orderId
     * @return
     */
    public OrderVO queryOrderById(Long orderId) {
        //查询订单信息
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            // 不存在
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        // 判断用户id是否正确
        Long userId = UserHolder.getUser();
        if(!userId.equals(order.getUserId())){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        //查询 订单详情信息
        OrderDetail detail = new OrderDetail();
        detail.setOrderId(orderId);
        List<OrderDetail> details = detailMapper.select(detail);
        if(CollectionUtils.isEmpty(details)){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        // 3.查询订单状态
        OrderLogistics logistics = logisticsMapper.selectByPrimaryKey(orderId);
        if (logistics == null) {
            // 不存在
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        //添加进去

        // 4.封装数据
        OrderVO orderVO = BeanHelper.copyProperties(order, OrderVO.class);
        orderVO.setDetailList(BeanHelper.copyWithCollection(details, OrderDetailVO.class));
        orderVO.setLogistics(BeanHelper.copyProperties(logistics, OrderLogisticsVO.class));
        return orderVO;
    }


    public static final String PAY_URL_KEY = "pay:url:";
    /**
     * 根据订单的id 查询支付的url  通过WXpay 工具类
     * @param orderId
     * @return
     */
    public String queryUrlByOrderId(Long orderId) {
        //向微信 发起请求 请求参数中 需要携带的参数有 请求体 请求金额

        String key = PAY_URL_KEY + orderId;
        //先去redis中取出
        String checkUrl = redisTemplate.boundValueOps(key).get();
        if (StringUtils.isNoneBlank(checkUrl)) {
            return checkUrl;
        }
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            //如果订单状态为空
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }

        //判断订单的状态是否为未支付
        Integer status = order.getStatus();
        if (status != OrderStatusEnum.INIT.value()) {
            throw new LyException(ExceptionEnum.INVALID_ORDER_STATUS);
        }

        String url = payHelper.createOrder(orderId, order.getActualFee(), "乐优商城");
        //为了防止用户 多次请求 所以将当前的地址 存入redis  这样每次用户请求 都是这个
        BoundValueOperations<String, String> valueOps = redisTemplate.boundValueOps(key);
        valueOps.set(url,2, TimeUnit.HOURS);
        return url;

    }

    public void notifyPay(Map<String, String> result) {

        // 1 签名校验
        try {
            payHelper.isValidSign(result);
        }catch (Exception e){
            log.error("【微信回调】微信签名有误！, result: {}",result, e);
            throw new LyException(ExceptionEnum.INVALID_NOTIFY_SIGN, e);
        }

        // 2、业务校验
        payHelper.checkResultCode(result);

        // 3 校验金额数据
        String totalFeeStr = result.get("total_fee");
        String tradeNo = result.get("out_trade_no");
        if(StringUtils.isEmpty(totalFeeStr) || StringUtils.isEmpty(tradeNo)){
            // 回调参数中必须包含订单编号和订单金额
            throw new LyException(ExceptionEnum.INVALID_NOTIFY_PARAM);
        }

        // 3.1 获取结果中的金额
        long totalFee = Long.valueOf(totalFeeStr);
        // 3.2 获取订单
        Long orderId = Long.valueOf(tradeNo);
        Order order = orderMapper.selectByPrimaryKey(orderId);
        // 3.3.判断订单的状态，保证幂等
        if(!order.getStatus().equals(OrderStatusEnum.INIT.value())){
            // 订单已经支付，返回成功
            return;
        }
        // 3.4.判断金额是否一致
        if(totalFee != /*order.getActualPay()*/ 1){
            // 金额不符
            throw new LyException(ExceptionEnum.INVALID_NOTIFY_PARAM);
        }

        // 4 修改订单状态
        Order orderStatus = new Order();
        orderStatus.setStatus(OrderStatusEnum.PAY_UP.value());
        orderStatus.setOrderId(orderId);
        orderStatus.setPayTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(orderStatus);
        if(count != 1){
            log.error("【微信回调】更新订单状态失败，订单id：{}", orderId);
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        log.info("【微信回调】, 订单支付成功! 订单编号:{}", orderId);

    }

    /**
     * 根据订单id 查询订单的状态
     * @param orderId
     * @return
     */
    public Integer queryOrderStatusByOrderId(Long orderId) {

        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new LyException(ExceptionEnum.INVALID_ORDER_STATUS);
        }
        return order.getStatus();


    }
}
