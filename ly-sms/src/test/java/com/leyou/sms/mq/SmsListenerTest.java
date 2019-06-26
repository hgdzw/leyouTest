package com.leyou.sms.mq;

import com.leyou.common.constants.MQConstants;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * @Author: dzw
 * @Date: 2019/6/26 10:28
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsListenerTest {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void sendMsg() throws InterruptedException {

        HashMap<String, String> map = new HashMap<>();

        map.put("phone", "13285517971");
        map.put("code", "520211");

        amqpTemplate.convertAndSend(MQConstants.Exchange.SMS_EXCHANGE_NAME
        ,MQConstants.RoutingKey.VERIFY_CODE_KEY,map);

        Thread.sleep(5000);
    }

}
