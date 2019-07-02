package com.leyou.cart.service;

import com.leyou.cart.entity.Cart;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.threadLocals.UserHolder;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: dzw
 * @Date: 2019/7/2 21:05
 * @Version 1.0
 */
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate template;

    private static final String KEY_PREFIX = "ly:cart:uid:";

    public void addCart(Cart cart) {

        if (cart == null) {
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }

        //将数据传递到redis  数据结构  map<userid,map<skuid,carparam>>

        //1.查询用户添加的商品是否存在
        //存在  则直接修改数量后写回Redis
        //不存在 新建一条数据，然后写入Redis
        //获取当前用户
        String localName = KEY_PREFIX + UserHolder.getUser();

        //获取id 并操作当前对象
        BoundHashOperations<String, String, String> hashOps = template.boundHashOps(localName);

        //获取skuid作为key
        String skuId = cart.getSkuId().toString();

        //获取当前 key对应的值
        Boolean aBoolean = hashOps.hasKey(skuId);
        //获取数据
        Integer num = cart.getNum();

        if (aBoolean && aBoolean!=null){
            //如果有的话  获取其中的数目 加上传递进来的数目
            cart = JsonUtils.toBean(hashOps.get(skuId), Cart.class);
            cart.setNum(cart.getNum() + num);
        }
        hashOps.put(skuId, JsonUtils.toString(cart));
    }

    /**
     * 用户获取当前购物车列表
     * @return
     */
    public List<Cart> queryCart() {


        //从UserHodel 中获取用户的信息
        String localName = KEY_PREFIX + UserHolder.getUser();

        //判断是否存再用户
        Boolean aBoolean = this.template.hasKey(localName);
        if (!aBoolean && aBoolean==null) {
            //不存在直接返回
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        //从redis中获取
        BoundHashOperations<String, String, String> hashOps = template.boundHashOps(localName);

        //判断是否有数据
        Long size = hashOps.size();
        if (size == 0) {
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        List<String> keys = hashOps.values();
        //如果获取到  返回
        return keys.stream().map(s -> JsonUtils.toBean(s, Cart.class)).collect(Collectors.toList());

    }
}
