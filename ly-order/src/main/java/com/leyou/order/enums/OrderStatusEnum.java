package com.leyou.order.enums;

/**
 * @Author: dzw
 * @Date: 2019/7/3 10:34
 * @Version 1.0
 */
public enum OrderStatusEnum {
    INIT(1,"初始化，未付款"),
    PAY_UP(2, "已付款，未发货"),
    DELIVERED(3, "已发货，未确认"),
    CONFIRMED(4, "已确认,未评价"),
    CLOSED(5, "已关闭"),
    RATED(6, "已评价，交易结束");

    private Integer value;
    private String msg;

    OrderStatusEnum(int value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public Integer value(){
        return this.value;
    }

    public String msg(){
        return msg;
    }

}
