package com.leyou.common.utils;


import com.leyou.common.auth.entity.AppInfo;
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
     * 解析token
     */
    @Test
    public void parseToken() throws Exception {

        String token = "eyJhbGciOiJSUzI1NiJ9.eyJ1c2VyIjoie1wiaWRcIjo3LFwic2VydmljZU5hbWVcIjpcImFwaS1nYXRld2F5XCIsXCJ0YXJnZXRMaXN0XCI6WzEsMiwzLDQsNSw2LDcsOCw5LDEwXX0iLCJqdGkiOiJPVFZsTkRreFptTXRNMlZsTUMwMFlUSmhMVGswWldFdFl6ZG1aVFF5TVRBMU9USTMiLCJleHAiOjE1NjIxMjMyMzN9.mGamDvl8Y61Yt5fDCFdSnNXVfrJ1ioEmwt8SXHWEDt-bvvucYkP-crusNCqtfdCYMHTTWwQsnMBjhMsztOy2c9xiJ1lYe55nQYW_QZn2pqT5KIDPk_iXYpIDsDrVeOeIpyAFmdJHE51Nl6IxM3DbUPoZ7EF9ILO2mtVqz3x6BmbGA57KEZqZkX4OsGvMe8ZcAyZRQUzFnxO0GCFl7RTXcVGboTPq1rqK7O53xX01Ba878Hz3QDYWzizT8NGI1pSGHukajf2aFxxFgTamLO-1ZudPTlEHtP3bxbXtDGxiz8pLSUNOvsM1Op2aRxaTa36oDtnmpQZIL7HPbbTULTUFpw";
        Payload<AppInfo> fromToken1 = JwtUtils.getInfoFromToken(token, RsaUtils.getPublicKey(publicFileName), AppInfo.class);
        AppInfo userInfo = fromToken1.getUserInfo();
        System.out.println(userInfo);


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
