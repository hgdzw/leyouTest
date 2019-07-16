package com.leyou.common.constants;

/**
 * @author HuYi
 */
public abstract class MQConstants {

    public static final class Exchange {
        /**
         * 商品服务交换机名称
         */
        public static final String ITEM_EXCHANGE_NAME = "ly.item.exchange";
        //短信交换机
        public static final String SMS_EXCHANGE_NAME = "ly.sms.exchange";

        //购物车交换机
        public static final String CART_EXCHANGE_NAME = "ly.cart.exchange";



    }

    public static final class RoutingKey {
        /**
         * 商品上架的routing-key
         */
        public static final String ITEM_UP_KEY = "item.up";
        /**
         * 商品下架的routing-key
         */
        public static final String ITEM_DOWN_KEY = "item.down";

        /**
         * 清空下过单的购物车routing-key
         */
        public static final String CART_DOWN_KEY = "cart.down";

        /**
         * 短信
         */
        public static final String VERIFY_CODE_KEY = "sms.verify.code";
    }

    public static final class Queue{
        /**
         * 搜索服务，商品上架的队列
         */
        public static final String SEARCH_ITEM_UP = "search.item.up.queue";
        /**
         * 搜索服务，商品下架的队列
         */
        public static final String SEARCH_ITEM_DOWN = "search.item.down.queue";

        /**
         * 搜索服务，商品上架的队列
         */
        public static final String PAGE_ITEM_UP = "page.item.up.queue";
        /**
         * 搜索服务，商品下架的队列
         */
        public static final String PAGE_ITEM_DOWN = "page.item.down.queue";

        /**
         * 搜索服务，购物车清理的队列
         */
        public static final String CART_ITEM_DOWN = "cart.item.down.queue";

        /**
         * 短信
         */
        public static final String SMS_VERIFY_CODE_QUEUE = "sms.verify.code.queue";
    }
}
