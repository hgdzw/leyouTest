package com.leyou.cart.controller;

import com.leyou.cart.entity.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: dzw
 * @Date: 2019/7/2 21:02
 * @Version 1.0
 */
@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 这是购物车过来添加数据
     *
     * @param cart
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {

        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 获取用户购物车列表
     *
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Cart>> queryCart() {

        List<Cart> carts = this.cartService.queryCart();
        if (carts == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(carts);
    }

    /**
     * 修改购物车中商品的数量
     *
     * @param skuId
     * @param num
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> putCartNum(@RequestParam("skuId") Long skuId,
                                           @RequestParam("num") Integer num) {

        cartService.putCartNum(skuId, num);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 根据skuid 删除购物车中的数据
     *
     * @param skuId
     * @return
     */
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCartBySkuId(@RequestParam("skuId") Long skuId) {

        cartService.deleteCartBySkuId(skuId);
        return ResponseEntity.ok().build();

    }

    /**
     * 批量添加购物车数据
     * @param carts
     * @return
     */
    @PostMapping("list")
    public ResponseEntity<Void> addCartList(@RequestBody List<Cart> carts){

        cartService.addCartList(carts);

        return ResponseEntity.ok().build();

    }
}
