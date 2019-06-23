package com.leyou.client;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: dzw
 * @Date: 2019/6/19 10:08
 * @Version 1.0
 */
@FeignClient(value = "item-service")
public interface ItemClient {


    /**
     * 根据 id列表 查询品牌
     * @param idList
     * @return
     */
    @GetMapping("/brand/list")
    List<BrandDTO> queryBrandsByIds(@RequestParam("ids")List<Long> idList);

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("/brand/{id}")
    BrandDTO queryBrandById(@PathVariable("id") Long id);

    /**
     * 根据spuid 查询 spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public SpuDTO querySpuById(@PathVariable("id")Long id);

    /**
     * 根据分类id 查询商品规格和规格组的信息
     * @param cid
     * @return
     */
    @GetMapping("/spec/of/category")
    public List<SpecGroupDTO> querySpecsByCid(@RequestParam("id")Long cid);

    /**
     * 根据id的集合查询商品分类
     * @param idList 商品分类的id集合
     * @return 分类集合
     */
    @GetMapping("/category/list")
    List<CategoryDTO> queryCategoryByIds(@RequestParam("ids") List<Long> idList);

    /**
     * 分页查询spu
     * @param page 当前页
     * @param rows 每页大小
     * @param saleable 上架商品或下降商品
     * @param key 关键字
     * @return 当前页商品数据
     */
    @GetMapping("spu/page")
    PageResult<SpuDTO> querySpuByPage(@RequestParam(value = "key",required = false)String key,
                                                             @RequestParam(value = "saleable",required = false)Boolean saleable,
                                                             @RequestParam(value = "page",defaultValue = "1")Integer page,
                                                             @RequestParam(value = "rows",defaultValue = "5")Integer rows);


    /**
     * 根据spuid 查询spu_datail的数据
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail")
    SpuDetailDTO queryDatailBySpId(@RequestParam("id")Long spuId);


    /**
     * 根据spuid查询spu的信息
     * @param spuId
     * @return
     */
    @GetMapping("sku/of/spu")
    List<SkuDTO> querySkuBySpuId(@RequestParam("id")Long spuId);

    /**
     * 根据 cid  或者 组id  或者搜索 获取数据
     * @param gid   规格组id
     * @param cid   商品分类id
     * @param searching   是否用于搜索   ？？？？
     * @return
     */
    @GetMapping("spec/params")
    List<SpecParamDTO> queryParamByGid(@RequestParam(value = "gid",required = false)Long gid,
                                                              @RequestParam(value = "cid", required = false) Long cid,
                                                              @RequestParam(value = "searching", required = false) Boolean searching);


    }
