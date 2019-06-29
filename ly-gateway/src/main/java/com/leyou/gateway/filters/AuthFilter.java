package com.leyou.gateway.filters;

import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import com.leyou.common.utils.RsaUtils;
import com.leyou.common.vo.Payload;
import com.leyou.common.vo.UserInfo;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Author: dzw
 * @Date: 2019/6/28 16:53
 * @Version 1.0
 */

@Slf4j
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})

//TODO 这里的过滤器 暂时先不开启
@Component

public class AuthFilter extends ZuulFilter {

    @Autowired
    private FilterProperties filterPro;

    @Autowired
    private JwtProperties pro;


    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER -1 ;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean shouldFilter() {

        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest req = ctx.getRequest();
        // 获取路径
        String requestURI = req.getRequestURI();
        // 判断白名单
        //TODO 先过滤
        return !isAllowPath(requestURI);
//        return true;
    }

    /**
     * 用来判断这个路径是不是和配置文件中的路径一致
     * @param requestURI
     * @return
     */
    private boolean isAllowPath(String requestURI) {
        // 定义一个标记
        boolean flag = false;
        // 遍历允许访问的路径
        for (String path : this.filterPro.getAllowPaths()) {
            // 然后判断是否是符合
            if(requestURI.startsWith(path)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 作用是拦截请求 鉴定里面的token 看是否登录 和权限是什么
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        //具体过滤器流程
        //获取上下文
        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();

        //从请求头里面获取cookie 把token 拿出来
        String token = CookieUtils.getCookieValue(request, pro.getUser().getCookieName());

//        校验
        try{
            Payload<UserInfo> info = JwtUtils.getInfoFromToken(token, pro.getPublicKey(), UserInfo.class);

            //判断是否有效

            UserInfo userInfo = info.getUserInfo();
            //权限是什么
            String role = userInfo.getRole();

            //获取请求的路径
            StringBuffer url = request.getRequestURL();
            String method = request.getMethod();
            // TODO 判断权限，此处暂时空置，等待权限服务完成后补充
            log.info("【网关】用户{},角色{}。访问服务{} : {}，", userInfo.getUsername(), role, method, url);

        }catch (Exception e){
            //当登录失败的时候
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(403);
            log.error("非法访问，未登录，地址：{}", request.getRemoteHost(), e );


        }


        return null;
    }
}
