package com.leyou.order.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 用来加载配置文件中的配置的
 * @Author: dzw
 * @Date: 2019/7/3 11:00
 * @Version 1.0
 */
@Data
@ConfigurationProperties(prefix = "ly.worker")
public class IdWorkProperties {
    //当前机器id
    private long workerId;
    //序列号
    private long dataCenterId;

}
