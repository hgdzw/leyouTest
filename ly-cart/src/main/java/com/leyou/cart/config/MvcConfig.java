package com.leyou.cart.config;

import com.leyou.cart.inteceptor.UserIntercept;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: dzw
 * @Date: 2019/7/2 20:47
 * @Version 1.0
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer{

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserIntercept()).addPathPatterns("/**");
    }
}
