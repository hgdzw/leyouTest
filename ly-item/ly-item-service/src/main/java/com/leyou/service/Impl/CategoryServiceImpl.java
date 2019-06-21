package com.leyou.service.Impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.entity.Category;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.mapper.CategoryMapper;
import com.leyou.service.CategoryService;

import org.aspectj.apache.bcel.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Stream;

/**
 * @Author: dzw
 * @Date: 2019/6/12 16:15
 * @Version 1.0
 */
@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 这是商品里面根据id列表返回分类名称要用的
     * @param ids
     * @return
     */
    public List<CategoryDTO> queryListByIds(List<Long> ids){

        List<Category> list = categoryMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(list,CategoryDTO.class);
    }


    /**
     * 业务 逻辑 判断 pid  返回categoryDTO
     * @param pid
     * @return
     */
    @Override
    public List<CategoryDTO> queryListByParent(Long pid) {
        // 查询条件，mapper会把对象中的非空属性作为查询条件
        Category t = new Category();
        t.setParentId(pid);
        List<Category> list = categoryMapper.select(t);
        // 判断结果
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        // 使用自定义工具类，把Category集合转为DTO的集合
        return BeanHelper.copyWithCollection(list, CategoryDTO.class);
    }

    /**
     * 根据品牌id 查询对应分类信息
     * @param bid
     * @return
     */
    @Override
    public List<CategoryDTO> queryCategoryListByBrandId(Long bid) {

        List<Category> categories = categoryMapper.queryListByBrandId(bid);
        //判断结果
        if (CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        //把结果变成categorydto
        List<CategoryDTO> list = BeanHelper.copyWithCollection(categories, CategoryDTO.class);
        return list;
    }

    /**
     * 根据id的集合查询商品分类
     * @param idList
     * @return
     */
    @Override
    public List<CategoryDTO> queryCategoryByIds(List<Long> idList) {

        List<Category> categories = categoryMapper.selectByIdList(idList);
        if (CollectionUtils.isEmpty(categories)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(categories,CategoryDTO.class);
    }
}
