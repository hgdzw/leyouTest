package com.leyou.auth.config;


import com.leyou.common.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@Slf4j
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties implements InitializingBean {
    /**
     * 公钥地址
     */
    private String pubKeyPath;
    /**
     * 私钥地址
     */
    private String priKeyPath;

    /**
     * 用户token相关属性
     */
    private UserTokenProperties user = new UserTokenProperties();

    private AppTokenProperties app = new AppTokenProperties();
    private PublicKey publicKey;
    private PrivateKey privateKey;

    @Data
    public class AppTokenProperties{
        private int expire;

        private Long id;

        /**
         * 秘钥
         */
        private String secret;

        /**
         * token 的请求头名称
         */
        private String headerName;

    }

    @Data
    public class UserTokenProperties {
        /**
         * token过期时长
         */
        private int expire;
        /**
         * 存放token的cookie名称
         */
        private String cookieName;
        /**
         * 存放token的cookie的domain
         */
        private String cookieDomain;

        private int minRefreshInterval;




    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {

            // 获取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
            this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥和私钥失败！", e);
            throw new RuntimeException(e);
        }
    }
}