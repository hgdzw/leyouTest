package com.leyou.cart.inteceptor;

import com.leyou.common.threadLocals.UserHolder;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import com.leyou.common.vo.Payload;
import com.leyou.common.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * @Author: dzw
 * @Date: 2019/7/2 20:33
 * @Version 1.0
 */
@Slf4j
public class UserIntercept implements HandlerInterceptor {


    private static final String COOKIE_NAME = "LY_TOKEN";
    /**
     * 前置拦截器
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //将token中的荷载信息拉取出来
        String token = CookieUtils.getCookieValue(request, COOKIE_NAME);
        try {
            Payload<UserInfo> userInfo = JwtUtils.getInfoFromToken(token, UserInfo.class);
            UserInfo info = userInfo.getUserInfo();
            //放用户的信息放进threadlocals中
            UserHolder.setUser(info.getId());
            return true;
        } catch (UnsupportedEncodingException e) {
            log.info("【购物车】 解析用户信息失败",e);
            return false;
        }
    }

    /**
     * 后置拦截器 将空间释放
     * @param request
     * @param response
     * @param handler
     * @param ex
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserHolder.removeUser();

    }
}
