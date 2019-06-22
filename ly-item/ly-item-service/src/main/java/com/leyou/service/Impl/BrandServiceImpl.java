package com.leyou.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.entity.Brand;
import com.leyou.item.dto.BrandDTO;
import com.leyou.mapper.BrandMapper;
import com.leyou.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Author: dzw
 * @Date: 2019/6/12 17:43
 * @Version 1.0
 */
@Service
public class BrandServiceImpl implements BrandService {



    @Autowired
    private BrandMapper brandMapper;


    /**
     * 这是商品中 根据品牌id 查询品牌的
     * @param bid
     * @return
     */
    public BrandDTO queryBrandByBid(Long bid){
        Brand brand = brandMapper.selectByPrimaryKey(bid);
        if (brand==null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return BeanHelper.copyProperties(brand,BrandDTO.class);
    }


    /**
     *
     * @param page  当前页
     * @param rows  每页大小
     * @param key   排序字段
     * @param sortBy    是否为降序
     * @param desc      搜索关键词
     * @return
     */
    @Override
    public PageResult<BrandDTO> queryBrandByPage(Integer page,
          Integer rows, String key, String sortBy, Boolean desc) {
        // 分页
        PageHelper.startPage(page, rows);
        // 过滤条件
        Example example = new Example(Brand.class);
        if(StringUtils.isNoneBlank(key)) {
            example.createCriteria().orLike("name", "%" + key + "%")
                    .orEqualTo("letter", key.toUpperCase());
        }
        // 排序
        if(StringUtils.isNoneBlank(sortBy)) {
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);// id desc
        }
        // 查询
        List<Brand> brands = brandMapper.selectByExample(example);

        // 判断是否为空
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        // 解析分页结果
        PageInfo<Brand> info = new PageInfo<>(brands);

        //

        List<BrandDTO> list = BeanHelper.copyWithCollection(brands, BrandDTO.class);

        // 返回
        return new PageResult<>(info.getTotal(), list);
    }

    /**
     * 新增品牌的逻辑
     * @param brandDTO
     * @param cids
     */
    @Override
    @Transactional   //这是 事务管理
    public void saveBrand(BrandDTO brandDTO, List<Long> cids) {

        //思路  先保存品牌的信息 再保存中间表
        //保存中间表的话 需要自定义mapper

        //这是把dto 映射到brand中
        Brand brand = BeanHelper.copyProperties(brandDTO, Brand.class);
        //组件回显  插入之后会把id 返回
        brand.setId(null);
        //保存
        int count = brandMapper.insertSelective(brand);
        if (count != 1){
            //新增失败
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        //新增 中间表 因为不能用通用mapper  所以需要自定义mapper
        count = brandMapper.insertCategoryBrand(brand.getId(), cids);
        if (count != cids.size()){
            //插入失败
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

    }

    /**
     * 更新brand 根据id更新
     * @param brandDTO
     * @param cids
     */
    @Override
    @Transactional  //开启事务
    public void updateBrand(BrandDTO brandDTO, List<Long> cids) {

        Brand brand = BeanHelper.copyProperties(brandDTO, Brand.class);

        //更新数据
        int count = brandMapper.updateByPrimaryKeySelective(brand);
        if (count != 1){
            //更新失败
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        //删除中间表数据
        brandMapper.deleteCategoryByBrand(brand.getId());

        //也要更新中间表
        count = brandMapper.insertCategoryBrand(brand.getId(),cids);
        if (count != cids.size()){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);

        }

    }

    /**
     * 根据id删除
     * @param bid
     */
    @Override
    @Transactional
    public void deleteBrandById(Long bid) {

        Brand brand = new Brand();
        brand.setId(bid);
        int i = brandMapper.deleteByPrimaryKey(brand);
        if (i != 1) {
            //删除失败
            throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }
        //删除品牌的同时 还要删除 中间表中关于这个品牌的数据
        brandMapper.deleteCategoryByBrand(bid);
    }

    /**
     * 根据cid 查询 品牌列表
     * @param cid
     * @return
     */
    @Override
    public List<BrandDTO> queryBrandByCid(Long cid) {

        List<Brand> brands = brandMapper.queryListByCid(cid);
        if (CollectionUtils.isEmpty(brands)){

            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        List<BrandDTO> brandDTOS = BeanHelper.copyWithCollection(brands, BrandDTO.class);
        return brandDTOS;
    }


    /**
     * 根据ids  查询品牌列表
     * @param idList
     * @return
     */
    @Override
    public List<BrandDTO> queryBrandsByIds(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }

        List<Brand> brands = brandMapper.selectByIdList(idList);
        if (CollectionUtils.isEmpty(brands)) {
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }

        return BeanHelper.copyWithCollection(brands,BrandDTO.class);

    }
}
