package com.leyou.controller;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.pojo.Item;
import com.leyou.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: dzw
 * @Date: 2019/6/11 21:28
 * @Version 1.0
 */
@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;


    @GetMapping("get")
    public String queryId(){

        return "请求成功";
    }

    @PostMapping("runtime")
    public ResponseEntity<Item> saveTest(Item item){
        if (null== item.getPrice()){
            //如果价格是空 则返回异常
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            throw new RuntimeException("价格不能为空");

        }
        Item result = itemService.saveItem(item);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);

    }


    @PostMapping("item")
    public ResponseEntity<Item> saveItem(Item item){
        if (null== item.getPrice()){
            //如果价格是空 则返回异常
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//            throw new RuntimeException("价格不能为空");
//            抛出自定义异常 里面有状态码 和信息
            throw new LyException(ExceptionEnum.PRICE_CANNOT_BE_NULL);
        }
        Item result = itemService.saveItem(item);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);

    }




}
