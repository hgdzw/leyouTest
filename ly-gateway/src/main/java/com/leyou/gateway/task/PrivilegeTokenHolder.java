package com.leyou.gateway.task;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.gateway.client.AuthClient;
import com.leyou.gateway.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: dzw
 * @Date: 2019/6/30 10:38
 * @Version 1.0
 */
@Slf4j
@Component
public class PrivilegeTokenHolder {

//    这是token
    private String token;

    @Autowired
    private JwtProperties pro;

    @Autowired
    private AuthClient authClient;
    /**
     * token刷新间隔
     */
    private static final long TOKEN_REFRESH_INTERVAL = 86400000L;

    /**
     * token获取失败后重试的间隔
     */
    private static final long TOKEN_RETRY_INTERVAL = 10000L;

    /**
     * 初始化token
     */
    @Scheduled(fixedDelay = TOKEN_REFRESH_INTERVAL)
    public void loadToken() throws InterruptedException {

        while (true) {
            try {
                //发起请求 获取token
                this.token = authClient.authorize(pro.getApp().getId(), pro.getApp().getSecret());
                log.info("【网关】获取token成功,{}",token);
                break;
            } catch (Exception e) {
                log.info("【网关】获取token失败 正在重试！");
            }
            // 休眠10秒，再次重试
            Thread.sleep(TOKEN_RETRY_INTERVAL);
        }
    }


    public String getToken() {
        return token;
    }

}
