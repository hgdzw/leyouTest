package com.leyou.user;

import com.leyou.user.dto.AddressDTO;
import com.leyou.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Author: dzw
 * @Date: 2019/6/28 11:32
 * @Version 1.0
 */
@FeignClient("user-service")
public interface UserClient {


    /**
     * 根据用户名密码查询用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    public UserDTO queryUser(@RequestParam("username")String username,
                                             @RequestParam("password")String password);


    /**
     * 根据
     * @param userId 用户id
     * @param id 地址id
     * @return 地址信息
     */
    @GetMapping("/address")
    AddressDTO queryAddressById(@RequestParam("userId") Long userId, @RequestParam("id") Long id);
}
