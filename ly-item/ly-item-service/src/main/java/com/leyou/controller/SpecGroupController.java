package com.leyou.controller;

import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.service.Impl.SpecServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: dzw
 * @Date: 2019/6/16 9:01
 * @Version 1.0
 */
@RestController
@RequestMapping("spec")
public class SpecGroupController {


    @Autowired
    private SpecServiceImpl specService;

    /**
     * 根据cid 查询规格组的信息
     * @param cid
     * @return
     */
    @GetMapping("/groups/of/category/")
    public ResponseEntity<List<SpecGroupDTO>> getSpecGroupByPid(@RequestParam("id")Long cid){

        return ResponseEntity.status(HttpStatus.OK).body(specService.queryGroupByCid(cid));
    }



    /**
     * 根据分类id 查询规格组和规格参数的信息
     * @param cid
     * @return
     */
    @GetMapping("/of/category")
    public ResponseEntity<List<SpecGroupDTO>> querySpecsByCid(@RequestParam("id")Long cid){

        return ResponseEntity.ok(specService.querySpecsByCid(cid));

    }


    /**
     * 根据 cid  或者 组id  或者搜索 获取数据
     * @param gid   规格组id
     * @param cid   商品分类id
     * @param searching   是否用于搜索   ？？？？
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParamDTO>> queryParamByGid(@RequestParam(value = "gid",required = false)Long gid,
                                                              @RequestParam(value = "cid", required = false) Long cid,
                                                              @RequestParam(value = "searching", required = false) Boolean searching){

        return ResponseEntity.ok(specService.querySpecParams(gid,cid,searching));
    }

    /**
     * 新增规格参数
     * @param specParamDTO
     * @return
     */
    @PostMapping("/param")
    public ResponseEntity<Void> addParam(@RequestBody SpecParamDTO specParamDTO){

        specService.saveParam(specParamDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/param/{id}")
    public ResponseEntity<Void> deleteParam(@PathVariable("id") Long id){
        specService.deleteParam(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }



}
