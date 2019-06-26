package com.leyou.thymeleaf.listing;

import com.leyou.common.constants.MQConstants;
import com.leyou.thymeleaf.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: dzw
 * @Date: 2019/6/25 20:52
 * @Version 1.0
 */

@Component
public class ItemListin {

    @Autowired
    private PageService pageService;

    /**
     * 上架的处理逻辑
     * 新增页面
     * @param spuId
     */
    @RabbitListener(bindings =@QueueBinding(
       value = @Queue(name = MQConstants.Queue.PAGE_ITEM_UP,durable = "true"),
       exchange = @Exchange(name = MQConstants.Exchange.ITEM_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
       key = MQConstants.RoutingKey.ITEM_UP_KEY
    ) )
    public void InsertIndex(Long spuId){
        if (spuId!=null) {
            pageService.createIndexHtml(spuId);
        }
    }


    /**
     * 这是下架的处理逻辑
     * 删除页面
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstants.Queue.PAGE_ITEM_DOWN,durable = "true"),
            exchange = @Exchange(name = MQConstants.Exchange.ITEM_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = MQConstants.RoutingKey.ITEM_DOWN_KEY
    ))
    public void deleteIndex(Long spuId){
        if (spuId!=null) {
            pageService.deleteIndexHtml(spuId);
        }
    }

}
