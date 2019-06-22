package com.leyou.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.service.Impl.BrandServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: dzw
 * @Date: 2019/6/12 17:36
 * @Version 1.0
 */
@RestController
@RequestMapping("brand")
public class BrandController {


    @Autowired
    private BrandServiceImpl brandService;



    @GetMapping("/{id}")
    public BrandDTO queryBrandById(@PathVariable("id") Long id){

        return brandService.queryBrandByBid(id);
    }


    /**
     * 根据id列表 查询brand
     * @param idList
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<BrandDTO>> queryBrandsByIds(@RequestParam("ids")List<Long> idList){

        return ResponseEntity.ok(brandService.queryBrandsByIds(idList));
    }

    /**
     * 根据cid 查询品牌的信息
     * @return
     */
    @GetMapping("/of/category")
    public ResponseEntity<List<BrandDTO>> queryBrandByCid(@RequestParam("id")Long cid){

        return ResponseEntity.ok(brandService.queryBrandByCid(cid));
    }

    /**
     * 更新品牌 和新增类似 但是多了 id
     * @param brandDTO
     * @param cids
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateBrand(BrandDTO brandDTO,@RequestParam("cids")List<Long> cids){
        brandService.updateBrand(brandDTO,cids);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBrandById(@RequestParam("id")Long bid){

        brandService.deleteBrandById(bid);

        return ResponseEntity.status(HttpStatus.OK).build();

    }


    /**
     *  查询数据 返回分页信息
     * @param page  当前页码
     * @param rows  每页大小
     * @param key   搜索字段
     * @param sortBy    排序字段
     * @param desc  是否为降序
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<BrandDTO>> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1")Integer page,
            @RequestParam(value = "rows", defaultValue = "5")Integer rows,
            @RequestParam(value = "key", required = false)String key,
            @RequestParam(value = "sortBy", required = false)String sortBy,
            @RequestParam(value = "desc", defaultValue = "false")Boolean desc
    ){
        return ResponseEntity
                .ok(brandService.queryBrandByPage(page,rows, key, sortBy, desc));
    }

    /**
     * 新增品牌的话 需要传品牌的基本信息 和品牌的所属分类的id  存分类的
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(BrandDTO brand, @RequestParam("cids")List<Long> cids){

        brandService.saveBrand(brand,cids);


        return ResponseEntity.status(HttpStatus.CREATED).build();
}

    @PostMapping("post")
    public String postRequest(String name){

        return name;
    }

}
