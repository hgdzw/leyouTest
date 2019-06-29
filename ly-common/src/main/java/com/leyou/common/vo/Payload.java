package com.leyou.common.vo;

import lombok.Data;

import java.util.Date;

/**
 * 用来封装荷载对象中用户的信息的
 * @author dzw
 */
@Data
public class Payload<T> {
    private String id;
    private T userInfo;
    private Date expiration;
}