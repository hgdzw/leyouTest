package com.leyou.order.controller;

import com.leyou.order.dto.OrderDTO;
import com.leyou.order.service.OrderService;
import com.leyou.order.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: dzw
 * @Date: 2019/7/3 10:52
 * @Version 1.0
 */
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 传递基本的参数 过来生成订单
     * @param orderDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO) {

        return ResponseEntity.ok(orderService.createOrder(orderDTO));

    }

    /**
     * 查询订单的数据 根据订单id
     * @param orderId
     * @return  返回的里面包含了很多信息  有订单的信息  地址的信息 物流的信息
     */
    @GetMapping("{id}")
    public ResponseEntity<OrderVO> queryOrderById(@RequestParam("id")Long orderId){


        return ResponseEntity.ok(orderService.queryOrderById(orderId));
    }

    /**
     * 根据订单的id  查询对应的支付的url
     * @param orderId
     * @return  微信支付的url
     */
    @GetMapping("/url/{id}")
    public ResponseEntity<String> queryUrlByOrderId(@PathVariable("id")Long orderId){

        return ResponseEntity.ok(orderService.queryUrlByOrderId(orderId));
    }

    @GetMapping("state/{id}")
    public ResponseEntity<Integer> queryOrderStatusByOrderId(@RequestParam("id") Long orderId) {

        return ResponseEntity.ok(orderService.queryOrderStatusByOrderId(orderId));
    }



}
