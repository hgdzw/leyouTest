package com.leyou.web;

import com.leyou.common.vo.PageResult;
import com.leyou.dto.GoodsDTO;
import com.leyou.dto.SearchRequest;
import com.leyou.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: dzw
 * @Date: 2019/6/20 11:29
 * @Version 1.0
 */
@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;
    /**
     * 根据搜索数据 去索引库中搜索
     * @param searchRequest
     * @return
     */
    @PostMapping("page")
    public ResponseEntity<PageResult<GoodsDTO>> queryGoodPageBySearch(@RequestBody SearchRequest searchRequest){

        return ResponseEntity.status(HttpStatus.OK).body(searchService.queryGoodPageBySearch(searchRequest));

    }

    @GetMapping("/get")
    public ResponseEntity<Void> queryGet(@RequestParam("id")Long id){

        return ResponseEntity.ok().build();
    }


}
