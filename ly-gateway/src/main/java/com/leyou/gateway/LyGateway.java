package com.leyou.gateway;

import com.leyou.gateway.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author: dzw
 * @Date: 2019/6/11 11:22
 * @Version 1.0
 */
@SpringCloudApplication
@EnableZuulProxy
@EnableFeignClients   //开启fen注解
//开启定时任务 去auth获取token
@EnableScheduling
//将 jetconfig 注入
@EnableConfigurationProperties(JwtProperties.class)
public class LyGateway {
    public static void main(String[] args) {
        SpringApplication.run(LyGateway.class,args);
    }
}
