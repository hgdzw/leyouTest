package com.leyou.controller;

import com.leyou.entity.Category;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.service.CategoryService;
import com.leyou.service.Impl.CategoryServiceImpl;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: dzw
 * @Date: 2019/6/12 16:13
 * @Version 1.0
 */
@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryServiceImpl categoryService;

    @GetMapping("/of/parent")
    public ResponseEntity<List<CategoryDTO>> queryByParentId(
            @RequestParam(value = "pid",defaultValue = "0")Long pid){
        return ResponseEntity.ok(this.categoryService.queryListByParent(pid));
    }

    /**
     * 根据3级分类id，查询1~3级的分类
     * @param id
     * @return
     */
    @GetMapping("/levels")
    public ResponseEntity<List<CategoryDTO>> queryAllByCid3(@RequestParam("id") Long id){

        return ResponseEntity.ok(categoryService.queryAllByCid3(id));
    }


    /**
     * 根据品牌id查询分类信息
     * @param bid   分类的id
     * @return
     */
    @GetMapping("/of/brand")
    public ResponseEntity<List<CategoryDTO>> queryCategoryListByBrandId(@RequestParam("id")Long bid){

        return ResponseEntity.ok(this.categoryService.queryCategoryListByBrandId(bid));

    }
    /**
     * 根据id的集合查询商品分类
     * @param idList 商品分类的id集合
     * @return 分类集合
     */
    @GetMapping("/list")
    List<CategoryDTO> queryCategoryByIds(@RequestParam("ids") List<Long> idList){

        return categoryService.queryCategoryByIds(idList);
    }






}
