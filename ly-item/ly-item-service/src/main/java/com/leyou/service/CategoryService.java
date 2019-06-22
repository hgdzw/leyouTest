package com.leyou.service;

import com.leyou.entity.Category;
import com.leyou.item.dto.CategoryDTO;

import java.util.List;

/**
 * @Author: dzw
 * @Date: 2019/6/12 16:15
 * @Version 1.0
 */
public interface CategoryService {

    public List<CategoryDTO> queryListByParent(Long pid);

    List<CategoryDTO> queryCategoryListByBrandId(Long bid);

    List<CategoryDTO> queryCategoryByIds(List<Long> idList);

    List<CategoryDTO> queryAllByCid3(Long id);

}
