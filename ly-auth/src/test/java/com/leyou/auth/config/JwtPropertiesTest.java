package com.leyou.auth.config;


import com.leyou.common.utils.JwtUtils;
import com.leyou.common.utils.RsaUtils;
import com.leyou.common.vo.Payload;
import com.leyou.common.vo.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.Assert.*;

/**
 * @Author: dzw
 * @Date: 2019/6/28 15:39
 * @Version 1.0
 */

public class JwtPropertiesTest {

    private static final String publicFileName="C:\\Users\\kafei\\.ssh\\rsa\\id_rsa.pub";
    private static final String privateFileName="C:\\Users\\kafei\\.ssh\\rsa\\id_rsa";

    @Test
    public void TestJwt() throws Exception {

        //获取私钥 用来加密token的
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateFileName);

        UserInfo userInfo = new UserInfo();
        userInfo.setId(1l);
        userInfo.setUsername("xiaowang");
        //生成令牌
        String s = JwtUtils.generateTokenExpireInMinutes(userInfo, privateKey, 5);
        System.out.println("token"+s);

        //获取公钥
        PublicKey publicKey = RsaUtils.getPublicKey(publicFileName);

        //解密token
        Payload<UserInfo> infoFromToken = JwtUtils.getInfoFromToken(s, publicKey, UserInfo.class);

        System.out.println("解密后的"+infoFromToken);


    }
}
