package com.leyou.cart.service;

import com.leyou.cart.entity.Cart;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.threadLocals.UserHolder;
import com.leyou.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: dzw
 * @Date: 2019/7/2 21:05
 * @Version 1.0
 */
@Service
@Slf4j
public class CartService {

    @Autowired
    private StringRedisTemplate template;

    private static final String KEY_PREFIX = "ly:cart:uid:";

    /**
     * 添加商品到购物车
     * @param cart
     */
    public void addCart(Cart cart) {

        if (cart == null) {
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }

        //将数据传递到redis  数据结构  map<userid,map<skuid,carparam>>

        //1.查询用户添加的商品是否存在
        //存在  则直接修改数量后写回Redis
        //不存在 新建一条数据，然后写入Redis
        //获取当前用户
        String localName = KEY_PREFIX + UserHolder.getUser();

        //获取id 并操作当前对象
        BoundHashOperations<String, String, String> hashOps = template.boundHashOps(localName);

        addCartInRedis(cart,hashOps);
        /*
        //获取skuid作为key
        String skuId = cart.getSkuId().toString();

        //获取当前 key对应的值
        Boolean aBoolean = hashOps.hasKey(skuId);
        //获取数据
        Integer num = cart.getNum();

        if (aBoolean && aBoolean!=null){
            //如果有的话  获取其中的数目 加上传递进来的数目
            cart = JsonUtils.toBean(hashOps.get(skuId), Cart.class);
            cart.setNum(cart.getNum() + num);
        }
        hashOps.put(skuId, JsonUtils.toString(cart));*/
    }

    /**
     * 用户获取当前购物车列表
     * @return
     */
    public List<Cart> queryCart() {


        //从UserHodel 中获取用户的信息
        String localName = KEY_PREFIX + UserHolder.getUser();

        //判断是否存再用户
        Boolean aBoolean = this.template.hasKey(localName);
        if (!aBoolean && aBoolean==null) {
            //不存在直接返回
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        //从redis中获取
        BoundHashOperations<String, String, String> hashOps = template.boundHashOps(localName);

        //判断是否有数据
        Long size = hashOps.size();
        if (size == 0) {
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        List<String> keys = hashOps.values();
        //如果获取到  返回
        return keys.stream().map(s -> JsonUtils.toBean(s, Cart.class)).collect(Collectors.toList());

    }

    /**
     * 修改购物车中商品的数量
     * @param skuId
     * @param num
     */
    public void putCartNum(Long skuId, Integer num) {

        //从redis中获取当前用户的对象
        String key = KEY_PREFIX + UserHolder.getUser();
        //绑定redis key为这个
        BoundHashOperations<String, String, String> hashOps = template.boundHashOps(key);

        Boolean boo = hashOps.hasKey(skuId.toString());
        if (boo == null || !boo) {
            log.error("购物车商品不存在，用户：{}, 商品：{}", UserHolder.getUser(), skuId);
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }

        //获取对象
        String s = hashOps.get(skuId.toString());

        //将获取的对象转化为可以修改的对象  修改  并且写回
        Cart cart = JsonUtils.toBean(s, Cart.class);
        cart.setNum(num);
        hashOps.put(skuId.toString(),JsonUtils.toString(cart));
    }

    /**
     * 根据skuid 删除购物车中的数据
     * @param skuId
     */
    public void deleteCartBySkuId(Long skuId) {
        deleteCartBySkuIdAndUserId(skuId,UserHolder.getUser());
    }

    /**
     * 根据用户的id 和skuid 删除 购物车的数据
     * @param skuId
     * @param userId
     */
    public void deleteCartBySkuIdAndUserId(Long skuId,Long userId){
        String key = KEY_PREFIX + userId;
        BoundHashOperations<String, String, String> hashOps = template.boundHashOps(key);
        //判断是否有这个数据
        Boolean aBoolean = hashOps.hasKey(skuId.toString());
        if (aBoolean) {
            //如果有 就移除
            Long delete = hashOps.delete(skuId.toString());
            if (delete != 1) {
                throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
            }
        }

    }

    /**
     * 批量添加购物车数据
     * @param carts
     */
    public void addCartList(List<Cart> carts) {
        if (CollectionUtils.isEmpty(carts)) {
            //如果为空
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        //绑定用户的redis
        String key = KEY_PREFIX + UserHolder.getUser();
        BoundHashOperations<String, String, String> hashOps = template.boundHashOps(key);
        //批量添加

        //判断是否在 如果不在 新增
        //如果在 获取数据 sum+
        for (Cart cart : carts) {
            //调用单个添加的方法
            addCartInRedis(cart,hashOps);
        }
    }

    /**
     * 添加一个商品到购物车
     * @param cart
     * @param hashOps
     */
    private void addCartInRedis(Cart cart, BoundHashOperations<String, String, String> hashOps) {
        // 获取商品id，作为hashKey
        String hashKey = cart.getSkuId().toString();
        // 获取数量
        int num = cart.getNum();
        // 判断要添加的商品是否存在
        Boolean boo = hashOps.hasKey(hashKey);
        if (boo != null && boo) {
            // 存在，修改数量
            cart = JsonUtils.toBean(hashOps.get(hashKey), Cart.class);
            cart.setNum(num + cart.getNum());
        }
        // 写入redis
        hashOps.put(hashKey, JsonUtils.toString(cart));
    }
}
