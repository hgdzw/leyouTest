package com.leyou.user.controller;

import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: dzw
 * @Date: 2019/6/26 11:44
 * @Version 1.0
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 校验手机号或者用户名唯一
     * @param data
     * @param type  1 是用户名  2 表示data 是手机号
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserAndPhone(@PathVariable("data")String data,
                                                     @PathVariable("type")Integer type){

        return ResponseEntity.ok(userService.checkData(data,type));
    }

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @PostMapping("code")
    public ResponseEntity<Void> sendPhone(@RequestParam("phone")String phone){

        userService.sendPhoneVerify(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    /**
     * 用户发送注册
     * @param user
     * @param code
     * @return
     */
    @PostMapping("register")
    public ResponseEntity<Void> userRegister(@Valid User user, @RequestParam("code")String code){
        userService.userRegister(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /**
     * 查询功能，根据参数中的用户名和密码查询指定用户并且返回用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    public ResponseEntity<UserDTO> queryUser(@RequestParam("username")String username,
                                             @RequestParam("password")String password){

        return ResponseEntity.ok(userService.queryUser(username,password));
    }





}
