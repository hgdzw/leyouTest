package com.leyou.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;

import java.util.List;

/**
 * @Author: dzw
 * @Date: 2019/6/12 17:43
 * @Version 1.0
 */
public interface BrandService {

    public PageResult<BrandDTO> queryBrandByPage(Integer page,Integer rows,String key,String sortBy,Boolean desc);

    void saveBrand(BrandDTO brand, List<Long> cids);

    void updateBrand(BrandDTO brandDTO, List<Long> cids);

    void deleteBrandById(Long bid);

    List<BrandDTO> queryBrandByCid(Long cid);

    List<BrandDTO> queryBrandsByIds(List<Long> idList);
}
