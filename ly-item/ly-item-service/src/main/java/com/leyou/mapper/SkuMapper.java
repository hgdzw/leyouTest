package com.leyou.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.entity.Sku;
import tk.mybatis.mapper.common.base.insert.InsertMapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * @Author: dzw
 * @Date: 2019/6/17 10:17
 * @Version 1.0
 */
public interface SkuMapper extends BaseMapper<Sku>,InsertListMapper<Sku> {
}
