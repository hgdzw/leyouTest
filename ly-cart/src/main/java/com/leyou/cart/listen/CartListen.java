package com.leyou.cart.listen;

import com.leyou.cart.service.CartService;
import com.leyou.common.constants.MQConstants;
import com.leyou.common.enums.ExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author: dzw
 * @Date: 2019/7/5 9:36
 * @Version 1.0
 */
@Component
@Slf4j
public class CartListen {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CartService cartService;

    /**
     * 监听队列  下单就把购物车的下单的删除掉
     * @param map
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConstants.Queue.CART_ITEM_DOWN,durable = "true"),
            exchange = @Exchange(value = MQConstants.Exchange.CART_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = MQConstants.RoutingKey.CART_DOWN_KEY

    ))
    public void reductionCart(Map<Long,List<Long>> map){
        //1.获取每一个id  然后遍历从redis中移除
        for (Map.Entry<Long, List<Long>> listEntry : map.entrySet()) {
            //取出用户的id
            Long userId = listEntry.getKey();
            //取出skuid的列表
            List<Long> value = listEntry.getValue();
            for (Long skuId : value) {
                cartService.deleteCartBySkuIdAndUserId(userId,skuId);
                log.info("【下单业务】下单成功 购物车的商品 已经成功删除！");
            }
        }
    }
}
