package com.leyou.user.config;

import com.leyou.user.interceptor.PrivilegeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: dzw
 * @Date: 2019/6/30 20:03
 * @Version 1.0
 */
//TODO 用户的鉴权开关在这里
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class MvcConfig implements WebMvcConfigurer{

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 这个将拦截器注册到register里面
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        PrivilegeInterceptor interceptor = new PrivilegeInterceptor(jwtProperties);
        //将拦截器注册进去
        registry.addInterceptor(interceptor).excludePathPatterns("/swagger-ui.html");
    }


}
