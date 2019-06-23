package com.leyou.thymeleaf.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @Author: dzw
 * @Date: 2019/6/23 10:16
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PageServiceTest {


    @Autowired
    private PageService pageService;
    @Test
    public void loadHtml() throws InterruptedException {

        Long[] arr = {96L, 114L, 124L, 125L, 141L};
        for (Long id : arr) {
            pageService.createIndexHtml(id);
            Thread.sleep(500);
        }



    }

}
