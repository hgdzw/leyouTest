package com.leyou.listing;

import com.leyou.client.ItemClient;
import com.leyou.common.constants.MQConstants;
import com.leyou.item.dto.SpuDTO;
import com.leyou.pojo.Goods;
import com.leyou.repository.GoodsRepository;
import com.leyou.service.SearchService;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: dzw
 * @Date: 2019/6/25 21:10
 * @Version 1.0
 */
@Component
public class Itemlisten {


    @Autowired
    private ItemClient itemClient;

    @Autowired
    private SearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConstants.Queue.SEARCH_ITEM_UP,durable = "true"),
            exchange = @Exchange(name = MQConstants.Exchange.ITEM_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = MQConstants.RoutingKey.ITEM_UP_KEY
    ))
    public void createIndex(Long spuId){
        if (spuId!=null){
            SpuDTO spuDTO = itemClient.querySpuById(spuId);
            Goods goods = searchService.buildGoods(spuDTO);
            searchService.createIndex(goods);

        }
    }
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstants.Queue.SEARCH_ITEM_DOWN, durable = "true"),
            exchange = @Exchange(
                    name = MQConstants.Exchange.ITEM_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = MQConstants.RoutingKey.ITEM_DOWN_KEY
    ))
    public void listenDelete(Long id){
        if(id != null){
            // 删除
            searchService.deleteById(id);
        }
    }




}
