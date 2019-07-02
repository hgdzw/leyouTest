package com.leyou;

import com.leyou.auth.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Author: dzw
 * @Date: 2019/6/28 11:19
 * @Version 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableConfigurationProperties(JwtProperties.class) //使配置类生效
@MapperScan("com.leyou.auth.mapper")
@EnableScheduling       //定时自己向自己获取token
public class AuthApplication {
    public static void main(String[] args) {

        SpringApplication.run(AuthApplication.class, args);
    }
}
