package com.leyou.common.utils;


import com.leyou.common.vo.Payload;
import com.leyou.common.vo.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;

import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.Assert.*;

/**
 * @Author: dzw
 * @Date: 2019/6/28 11:04
 * @Version 1.0
 */
public class BeanHelperTest {
    private static final String publicFileName="C:\\Users\\kafei\\.ssh\\rsa\\id_rsa.pub";
    private static final String privateFileName="C:\\Users\\kafei\\.ssh\\rsa\\id_rsa";


    @Test
    public void testRSA() throws Exception {

        // 生成密钥对
        RsaUtils.generateKey(publicFileName,privateFileName,"hello",2048);

        //获取私钥
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateFileName);
        System.out.println("private:"+privateKey);
        //获取公钥
        PublicKey publicKey = RsaUtils.getPublicKey(publicFileName);
        System.out.println("public:"+publicKey);

    }

    /**
     * 这是测试生成JWT的
     */
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
