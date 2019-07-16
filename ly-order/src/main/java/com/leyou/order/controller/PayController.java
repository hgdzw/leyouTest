package com.leyou.order.controller;

import com.leyou.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: dzw
 * @Date: 2019/7/5 20:59
 * @Version 1.0
 */

@RestController
@RequestMapping("pay")
@Slf4j
public class PayController {

    @Autowired
    private OrderService orderService;

    /**
     * 处理微信支付成功的回调函数
     * 因为微信默认是请求xml 文件 也是要返回xml的文件
     * 所以这里用的 底层消息转换器 将请求和返回转换成xml
     * @return
     */
    @PostMapping(value = "/wx/notify",produces = "application/xml")
    public Map<String,String> notifyPay(@RequestBody Map<String,String> result){

        // 处理回调
        log.info("[支付回调] 接收微信支付回调, 结果:{}", result);
        orderService.notifyPay(result);

        // 返回成功
        Map<String, String> msg = new HashMap<>();
        msg.put("return_code", "SUCCESS");
        msg.put("return_msg", "OK");
        return msg;

    }



}
