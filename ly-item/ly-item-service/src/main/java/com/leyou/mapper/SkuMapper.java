package com.leyou.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.entity.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * @Author: dzw
 * @Date: 2019/6/17 10:17
 * @Version 1.0
 */
public interface SkuMapper extends BaseMapper<Sku>,InsertListMapper<Sku> {
    @Update("UPDATE tb_sku SET stock = stock - #{num} WHERE id = #{id}")
    int minusStock(@Param("id") Long id, @Param("num") Integer num);
}
