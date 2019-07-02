package com.leyou.auth.controller;

import com.leyou.auth.service.AuthService;
import com.leyou.common.vo.UserInfo;
import com.netflix.client.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.HttpResource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: dzw
 * @Date: 2019/6/28 11:53
 * @Version 1.0
 */
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("login")
    public ResponseEntity<Void> userLogin(@RequestParam("username") String username,
                                          @RequestParam("password") String password,
                                          HttpServletResponse response) {

        authService.login(username, password, response);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 用户请求验证用户的登录状态
     * 状态从cookie里面取
     * @param request
     * @return  成功就返回用户信息
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(HttpServletRequest request,HttpServletResponse response){

        return ResponseEntity.ok(authService.verifyUser(request,response));



    }

    /**
     * 用户退出
     * @param response
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> loginOut(HttpServletResponse response, HttpServletRequest request) {

        authService.loginOut(request, response);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * 服务的授权  传id 和 秘钥 过来
     * @param id
     * @param secret
     * @return  传的token 里面包括AppInfo
     */
    @GetMapping("authorization")
    public ResponseEntity<String> authorize(@RequestParam("id")Long id,
                                            @RequestParam("secret")String secret){

        return ResponseEntity.ok(authService.authorize(id,secret));
    }

    @GetMapping("str")
    public ResponseEntity<String> str(@RequestParam("id")Long id,
                                            @RequestParam("secret")String secret){

        return ResponseEntity.ok("shoudao" + secret);

    }

}
