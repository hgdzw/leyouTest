package com.leyou.order.config;

import com.leyou.common.utils.IdWorker;
import com.leyou.order.properties.IdWorkProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: dzw
 * @Date: 2019/7/3 11:02
 * @Version 1.0
 */
@Configuration
@EnableConfigurationProperties(IdWorkProperties.class)
public class IdWokerConfig {

    @Bean
    public IdWorker getWork(IdWorkProperties prop){
        return new IdWorker(prop.getWorkerId(),prop.getDataCenterId());
    }

}
