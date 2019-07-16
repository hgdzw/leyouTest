package com.leyou.order.config;

import com.leyou.order.intercepts.UserIntercept;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: dzw
 * @Date: 2019/7/3 10:26
 * @Version 1.0
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * 将拦截器添加到mvc 的配置中
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserIntercept()).addPathPatterns("/**");
    }
}
