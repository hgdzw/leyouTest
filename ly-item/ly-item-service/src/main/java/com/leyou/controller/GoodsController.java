package com.leyou.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.entity.Sku;
import com.leyou.entity.Spu;
import com.leyou.entity.SpuDetail;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.service.Impl.GoodsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: dzw
 * @Date: 2019/6/16 11:40
 * @Version 1.0
 */
@RestController

public class GoodsController {

    @Autowired
    private GoodsServiceImpl goodsService;


    @GetMapping("spu/{id}")
    public SpuDTO querySpuById(@PathVariable("id")Long id){

        return goodsService.querySpuBySpId(id);
    }


    /**
     * 根据spuid查询spu的信息
     * @param spuId
     * @return
     */
    @GetMapping("sku/of/spu")
    public ResponseEntity<List<SkuDTO>> querySkuBySpuId(@RequestParam("id")Long spuId){

        return ResponseEntity.ok(goodsService.querySkuBySpuId(spuId));
    }



    /**
     * 根据skuid的列表 查询sku集合的信息
     * @param ids
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<SkuDTO>> querySkuByIds(@RequestParam("ids")List<Long> ids){

        return ResponseEntity.ok(goodsService.querySkuByIds(ids));

    }



    /**
     * 根据spuid 查询spu_datail的数据
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail")
    public ResponseEntity<SpuDetailDTO> queryDatailBySpId(@RequestParam("id")Long spuId){


        return ResponseEntity.ok(goodsService.queryDatailBySpId(spuId));

    }


    /**
     *
     * @param key  搜索关键字
     * @param saleable  上架或者下架商品
     * @param page  当前页数
     * @param rows  每页大小
     * @return
     */
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuDTO>> querySpuByPage(@RequestParam(value = "key",required = false)String key,
                                                             @RequestParam(value = "saleable",required = false)Boolean saleable,
                                                             @RequestParam(value = "page",defaultValue = "1")Integer page,
                                                             @RequestParam(value = "rows",defaultValue = "5")Integer rows){

        return ResponseEntity.ok(goodsService.querySpuByPage(key,saleable,page,rows));

    }


//    @PostMapping("goods")
//    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu,
//                                         @RequestBody Sku sku,
//                                         @RequestBody SpuDetail spuDetail){
//
//        goodsService.saveGoods(spu,spuDetail,sku);
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }


    /**
     * 新增商品
     * @param spuDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addGoods(@RequestBody SpuDTO spuDTO){

        goodsService.addGoods(spuDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    /**
     * 更新商品信息   比新增多一个id
     * @param spuDTO
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO){
        goodsService.updateGoodsById(spuDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * 根据spuid  来更新上下架的问题
     * @param spuId
     * @param saleable
     * @return
     */
    @PutMapping("spu/saleable")
    public ResponseEntity<Void> updateSaleable(@RequestParam("id")Long spuId,
                                               @RequestParam("saleable")Boolean saleable){

        goodsService.updateSaleable(spuId,saleable);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);


    }



}
