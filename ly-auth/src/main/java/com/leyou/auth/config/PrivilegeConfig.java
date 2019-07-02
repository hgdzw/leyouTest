package com.leyou.auth.config;

import com.leyou.auth.task.PrivilegeTokenHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 这个是feign 的拦截器 拦截了之后加入token
 * @Author: dzw
 * @Date: 2019/6/30 11:04
 * @Version 1.0
 */
@Configuration
public class PrivilegeConfig {

    @Bean
    public RequestInterceptor requestInterceptor(
            JwtProperties jwtProp, PrivilegeTokenHolder tokenHolder){

        return new PrivilegeInterceptor(jwtProp, tokenHolder);
    }
    private class PrivilegeInterceptor implements RequestInterceptor{

        private JwtProperties jwtProp;

        private PrivilegeTokenHolder tokenHolder;

        public PrivilegeInterceptor(JwtProperties jwtProp, PrivilegeTokenHolder tokenHolder) {
            this.jwtProp = jwtProp;
            this.tokenHolder = tokenHolder;
        }


        @Override
        public void apply(RequestTemplate requestTemplate) {
            //外部将需要的参数 注入进来了  现在只需要向请求头中添加token 就行了
            requestTemplate.header(jwtProp.getApp().getHeaderName(), tokenHolder.getToken());
        }
    }


}
