package com.leyou.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.entity.Category;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: dzw
 * @Date: 2019/6/12 16:17
 * @Version 1.0
 */
public interface CategoryMapper extends BaseMapper<Category> {
    List<Category> queryListByBrandId(@Param("id") Long bid);
}
