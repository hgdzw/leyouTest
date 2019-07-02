package com.leyou.auth.task;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.AppInfo;
import com.leyou.auth.mapper.ApplicationMapper;
import com.leyou.common.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时任务 获取token
 * @Author: dzw
 * @Date: 2019/6/30 11:07
 * @Version 1.0
 */
@Slf4j
@Component
public class PrivilegeTokenHolder {

    private String token;

    @Autowired
    private ApplicationMapper mapper;
    @Autowired
    private JwtProperties pro;

    /**
     * token刷新间隔
     */
    private static final long TOKEN_REFRESH_INTERVAL = 86400000L;


    @Scheduled(fixedDelay = TOKEN_REFRESH_INTERVAL)
    public void loadToken() throws InterruptedException{
        try {
            // 向ly-auth发起请求，获取JWT
            AppInfo appInfo = new AppInfo();
            appInfo.setId(pro.getApp().getId());
            //添加可访问列表
            List<Long> target = mapper.queryTargetById(pro.getApp().getId());
            appInfo.setTargetList(target);
            this.token = JwtUtils.generateTokenExpireInMinutes(appInfo, pro.getPrivateKey(), pro.getApp().getExpire());
            log.info("【授权中心】定时获取token成功，{}",token);
        } catch (Exception e) {
            log.info("【授权中心】定时获取token失败");
        }

    }
    public String getToken() {
        return token;
    }


}
