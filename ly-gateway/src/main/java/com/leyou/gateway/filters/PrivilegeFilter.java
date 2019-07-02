package com.leyou.gateway.filters;

import com.leyou.gateway.config.JwtProperties;
import com.leyou.gateway.task.PrivilegeTokenHolder;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * 这个过滤器是用来增加请求头的  里面增加token
 * @Author: dzw
 * @Date: 2019/6/30 10:53
 * @Version 1.0
 */
@Slf4j
@Component
public class PrivilegeFilter extends ZuulFilter{

    //用来读取请求头名称的
    @Autowired
    private JwtProperties pro;

    //用来读取获取的token的
    @Autowired
    private PrivilegeTokenHolder tokenHolder;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    /**
     * 我们放到自动过滤器之后执行 如果有权限访问 再加token
     * @return
     */
    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;
    }


    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 给请求头增加token
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        //获取上下文
        RequestContext context = RequestContext.getCurrentContext();
        //在请求头里面增加token
        context.addZuulRequestHeader(pro.getApp().getHeaderName(),tokenHolder.getToken());
        return null;
    }
}
