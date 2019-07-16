package com.leyou.order.intercepts;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.threadLocals.UserHolder;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import com.leyou.common.vo.Payload;
import com.leyou.common.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * 拦截器 拦截用户的请求并且解析用户token的数据
 * @Author: dzw
 * @Date: 2019/7/3 10:19
 * @Version 1.0
 */
@Slf4j
public class UserIntercept implements HandlerInterceptor{


//    private static String TOKEN_NAME = "LY_TOKEN";
    private static final String HEADER_NAME = "user_info";
    /**
     * 在执行handle之前执行   这个有问题 换了一个方法
     * @param request
     * @param response
     * @param handler
     * @return
     */
    /*@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //获取用户token中的 载荷信息
        String token = CookieUtils.getCookieValue(request, TOKEN_NAME);

        try {
            Payload<UserInfo> info = JwtUtils.getInfoFromToken(token, UserInfo.class);
            //将用户的id写入holder中
            UserHolder.setUser(info.getUserInfo().getId());
            return true;
        } catch (UnsupportedEncodingException e) {
            //解析失败
            log.info("【订单中心】解析用户信息失败",e);
            return false;
        }
    }*/


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取请求头
        String header = request.getHeader(HEADER_NAME);
        if (StringUtils.isBlank(header)) {
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
        // 保存用户信息
        UserHolder.setUser(Long.valueOf(header));
        return true;
    }


    /**
     * 在执行handle之后执行 移除内存中的
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
