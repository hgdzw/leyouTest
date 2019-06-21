package com.leyou.mapper;

import com.leyou.entity.Brand;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: dzw
 * @Date: 2019/6/12 17:35
 * @Version 1.0
 */
public interface BrandMapper extends Mapper<Brand> {

    //新增中间表的数据
    int insertCategoryBrand(@Param("bid")Long bid,@Param("ids") List<Long> ids);

    void deleteCategoryByBrand(@Param("bid") Long bid);

    //根据中间表数据
    List<Brand> queryListByCid(@Param("cid") Long cid);
}
