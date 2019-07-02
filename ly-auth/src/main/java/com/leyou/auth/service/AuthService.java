package com.leyou.auth.service;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.config.PasswordConfig;
import com.leyou.auth.entity.AppInfo;
import com.leyou.auth.entity.ApplicationInfo;
import com.leyou.auth.mapper.ApplicationMapper;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import com.leyou.common.utils.RsaUtils;
import com.leyou.common.vo.Payload;
import com.leyou.common.vo.UserInfo;
import com.leyou.user.UserClient;
import com.leyou.user.dto.UserDTO;
import com.sun.xml.internal.bind.v2.TODO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.PrivateKey;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: dzw
 * @Date: 2019/6/28 13:58
 * @Version 1.0
 */
@Service
public class AuthService {


    @Autowired
    private UserClient userClient;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtProperties pro;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;


    private static final String USER_ROLE = "role_user";
    /**
     * 用户登录的时候将 token写入cookie里面
     * @param username
     * @param password
     * @param response
     */
    public void login(String username, String password, HttpServletResponse response) {

        try {
            //1.验证用户名密码的有效性
            UserDTO userDTO = userClient.queryUser(username, password);

            if (userDTO != null) {


                UserInfo userInfo = new UserInfo(userDTO.getId(), username, USER_ROLE);

                //获取私钥
                PrivateKey privateKey = RsaUtils.getPrivateKey(pro.getPriKeyPath());
                //2.如果正确 那么获取用户对于的token
                String token = JwtUtils.generateTokenExpireInMinutes(userInfo, privateKey, pro.getUser().getExpire());


                //3.将token写入cooik
                /*//3.1创建cookie
                Cookie cookie = new Cookie("token", token);
                //3.2将token写进响应体
                response.addCookie(cookie);*/

                //将token写入cookie
                CookieUtils.newCookieBuilder()    //转化为builder
                        .response(response)     //添加response
                        .httpOnly(true)         //设置为true  不允许js操作cookie 防止XSS
                        .domain(pro.getUser().getCookieDomain())        //设置domain
                        .name(pro.getUser().getCookieName())        //设置name
                        .value(token)       //设置cookie值
                        .build();
            }
        }catch (Exception e){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
    }

    /**
     * 根据cookie中的值判断用户登录的状态
     * @param request
     * @param response
     * @return
     */
    public UserInfo verifyUser(HttpServletRequest request, HttpServletResponse response) {

        try {
            //1.获取cookie中的token
            String token = CookieUtils.getCookieValue(request, pro.getUser().getCookieName());

            //2.解析token
            Payload<UserInfo> fromToken = JwtUtils.getInfoFromToken(token, pro.getPublicKey(), UserInfo.class);


            //这里还要检验tokenid 是不是在redis 黑名单中
            String id = fromToken.getId();
            Boolean bool = redisTemplate.hasKey(id);
            if (bool != null && bool){
                throw new LyException(ExceptionEnum.UNAUTHORIZED);
            }
            //获取过期时间
            Date expiration = fromToken.getExpiration();
            //获取刷新时间

            DateTime refreshTime = new DateTime(expiration.getTime()).minusMinutes(pro.getUser().getMinRefreshInterval());

            // 判断是否已经过了刷新时间
            if (refreshTime.isBefore(System.currentTimeMillis())) {
                // 如果过了刷新时间，则生成新token
                token = JwtUtils.generateTokenExpireInMinutes(fromToken.getUserInfo(), pro.getPrivateKey(), pro.getUser().getExpire());
                // 写入cookie

                CookieUtils.newCookieBuilder()
                        // response,用于写cookie
                        .response(response)
                        // 保证安全防止XSS攻击，不允许JS操作cookie
                        .httpOnly(true)
                        // 设置domain
                        .domain(pro.getUser().getCookieDomain())
                        // 设置cookie名称和值
                        .name(pro.getUser().getCookieName()).value(token)
                        // 写cookie
                        .build();
            }
            //返回
            return fromToken.getUserInfo();




        }catch (Exception e){
            //报错就返回401
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
    }

    private static final String REDIS_TOKEN = "user_token";

    /**
     * 用户登出
     * @param request
     * @param response
     */
    public void loginOut(HttpServletRequest request, HttpServletResponse response) {
        
        //从request中获取cookie 并解析其中的token
        String token = CookieUtils.getCookieValue(request, pro.getUser().getCookieName());

        Payload<UserInfo> userInfo = JwtUtils.getInfoFromToken(token, pro.getPublicKey(), UserInfo.class);

        //获得id 和有效剩余市场
        String id = userInfo.getId();
        long resultTime = userInfo.getExpiration().getTime() - System.currentTimeMillis();

        //将token 放进redis 失效里 并设置删除时间
        if (resultTime > 5000) {
            redisTemplate.opsForValue().set(id,"",resultTime, TimeUnit.MILLISECONDS);

        }
        //将用户返回的response中的cookie 删除
        CookieUtils.deleteCookie(pro.getUser().getCookieName(),pro.getUser().getCookieDomain()
        ,response);
        
        
    }

    /**
     * 服务间的授权
     * @param id
     * @param secret
     * @return
     */
    public String authorize(Long id, String secret) {
        //根据查询身份信息
        ApplicationInfo info = applicationMapper.selectByPrimaryKey(id);

        if (info == null) {
            throw new LyException(ExceptionEnum.INVALID_SERVER_ID_SECRET);
        }
        //将密码进行比较
        if (!passwordEncoder.matches(secret, info.getSecret())) {
            //不匹配
            throw new LyException(ExceptionEnum.INVALID_SERVER_ID_SECRET);

        }
        //进行权限添加
        //查询权限
        List<Long> target = applicationMapper.queryTargetById(id);
        AppInfo appInfo = new AppInfo();
        appInfo.setId(id);
        appInfo.setServiceName(info.getServiceName());
        appInfo.setTargetList(target);
        //生成token
        String token = JwtUtils.generateTokenExpireInMinutes(appInfo, pro.getPrivateKey(), pro.getApp().getExpire());
        return token;

    }
}
