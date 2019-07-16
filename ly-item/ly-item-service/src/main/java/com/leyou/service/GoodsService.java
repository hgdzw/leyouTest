package com.leyou.service;

import com.leyou.common.vo.PageResult;
import com.leyou.entity.Sku;
import com.leyou.entity.Spu;
import com.leyou.entity.SpuDetail;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * @Author: dzw
 * @Date: 2019/6/16 11:41
 * @Version 1.0
 */
public interface GoodsService {

    PageResult<SpuDTO> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows);

    void saveGoods(Spu spu, SpuDetail spuDetail, Sku sku);

    void addGoods(SpuDTO spuDTO);

    void updateSaleable(Long spuId, Boolean saleable);

    SpuDetailDTO queryDatailBySpId(Long spuId);

    SpuDTO querySpuBySpId(Long spuId);

    List<SkuDTO> querySkuBySpuId(Long spuId);

    void updateGoodsById(SpuDTO spuDTO);

    List<SkuDTO> querySkuByIds(List<Long> ids);

    void minusStock(Map<Long, Integer> cartMap);
}
