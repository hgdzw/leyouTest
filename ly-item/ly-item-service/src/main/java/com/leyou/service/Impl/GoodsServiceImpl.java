package com.leyou.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.constants.MQConstants;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.config.RabbitConfig;
import com.leyou.entity.Sku;
import com.leyou.entity.Spu;
import com.leyou.entity.SpuDetail;
import com.leyou.item.dto.*;
import com.leyou.mapper.SkuMapper;
import com.leyou.mapper.SpuDetailMapper;
import com.leyou.mapper.SpuMapper;
import com.leyou.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: dzw
 * @Date: 2019/6/16 11:41
 * @Version 1.0
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private BrandServiceImpl brandService;

    //消息队列
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private CategoryServiceImpl categoryService;
    /**
     * 分页展示 商品spu信息
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @Override
    public PageResult<SpuDTO> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {


        //开启分页
        PageHelper.startPage(page,rows);

        //通用mapper 的高级查询
        Example example = new Example(Spu.class);

        //过滤
        if (StringUtils.isNoneBlank(key)) {
            example.createCriteria().andLike("name", "%" + key + "%");
        }
        Example.Criteria criteria2 = example.createCriteria();
        if (saleable!=null){
            criteria2.andEqualTo("saleable",saleable);

        }
        example.and(criteria2);

        example.setOrderByClause("update_time DESC");

        List<Spu> spus = spuMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(spus)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }

        //解析结果
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spus);

        List<SpuDTO> spuDTOS = BeanHelper.copyWithCollection(spus, SpuDTO.class);


        //处理分类名称和品牌名称   查询两个封装进spudto中
        for (SpuDTO spuDTO : spuDTOS) {
            String cateName = "";
            //分类名称的拼接  查询到好多的分类对象
            List<CategoryDTO> categoryDTOS = categoryService.queryListByIds(spuDTO.getCategoryIds());
            for (CategoryDTO categoryDTO : categoryDTOS) {
                String name = categoryDTO.getName();
                cateName+= name+"/";
            }
            spuDTO.setCategoryName(cateName);

            //品牌名称
            BrandDTO brandDTO = brandService.queryBrandByBid(spuDTO.getBrandId());
            spuDTO.setBrandName(brandDTO.getName());
        }
        return new PageResult<>(spuPageInfo.getTotal(),spuDTOS);
    }

    /**
     * 这是实现了新增商品的函数  自己定义的 接收参数的时候不行
     * @param spu
     * @param spuDetail
     * @param sku
     * @return
     */
    @Override
    @Transactional(rollbackFor = LyException.class)
    public void saveGoods(Spu spu, SpuDetail spuDetail, Sku sku) {

        //新增商品  新增spu  spudtail  sku 的商品

        //新增spu 的商品
        spu.setId(null);
        int insert = spuMapper.insertSelective(spu);
        if (insert != 1) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        //回显id 到spu关联起来
        spuDetail.setSpuId(spu.getId());

        //新增spudatil商品
        int insert1 = spuDetailMapper.insert(spuDetail);
        if (insert1 != 1) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);

        }
        //新增 sku的数据




    }


    /**
     * 新增商品的请求  其中还新增spudatil  和sku 表
     * @param spuDTO
     */
    @Override
    @Transactional
    public void addGoods(SpuDTO spuDTO) {

        //把spudto编程 spu
        Spu spu = BeanHelper.copyProperties(spuDTO, Spu.class);

        //新增spu表
        spu.setId(null);
        spu.setCreateTime(null);
        spu.setUpdateTime(null);
        int i = spuMapper.insertSelective(spu);
        if (i!=1){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();
        SpuDetail spuDetail1 = BeanHelper.copyProperties(spuDetail, SpuDetail.class);
        spuDetail1.setSpuId(spu.getId());
        //新增spudatil表
        int i1 = spuDetailMapper.insertSelective(spuDetail1);
        if (i1 != 1) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

        List<SkuDTO> skuDTOSs = spuDTO.getSkus();
        ArrayList<Sku> skus = new ArrayList<>();

        //循环把列表加到 skus 中
        for (SkuDTO dtoSs : skuDTOSs) {
            dtoSs.setSpuId(spu.getId());
            skus.add(BeanHelper.copyProperties(dtoSs,Sku.class));
        }

        skus.forEach(System.out::println);

        //批量新增
        int insertList = skuMapper.insertList(skus);
        if (insertList != skus.size()) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 根据id 更新商品的信息
     * 删除sku 新增 sku  更新 spu   spudatil
     * @param spuDTO
     */
    @Override
    public void updateGoodsById(SpuDTO spuDTO) {
        //判断id有没有传过来 把sku表删掉
        Long spuId = spuDTO.getId();
        if (spuId == null) {
            // 请求参数有误
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        // 1.删除sku
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        // 查询数量
        int size = skuMapper.selectCount(sku);
        if(size > 0) {
            // 删除
            int count = skuMapper.delete(sku);
            if(count != size){
                throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
            }
        }


        Spu spu = BeanHelper.copyProperties(spuDTO, Spu.class);
        //更新spu表
        //这里设置为空的话 应该就是走默认
        spu.setSaleable(null);
        spu.setCreateTime(null);
        spu.setUpdateTime(null);
        int i = spuMapper.updateByPrimaryKeySelective(spu);
        if (i!=1){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }

        //更新spudatail表
        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();
        SpuDetail spuDetail1 = BeanHelper.copyProperties(spuDetail, SpuDetail.class);
        spuDetail1.setSpuId(spuDTO.getId());
        spuDetail1.setCreateTime(null);
        spuDetail1.setUpdateTime(null);
        int i1 = spuDetailMapper.updateByPrimaryKeySelective(spuDetail1);
        if (i1!=1){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        // 4.新增sku   ？？？？
        /*
         * 1.查询skus
         * 2.将查询出来的skusdto 编程skus
         * 3.遍历skus 将它添加到sku表中
         *
         * */
        List<SkuDTO> dtoList = spuDTO.getSkus();
        // 处理dto
        List<Sku> skuList = dtoList.stream().map(dto -> {
            dto.setEnable(false);
            // 添加spu的id
            dto.setSpuId(spuId);
            return BeanHelper.copyProperties(dto, Sku.class);
        }).collect(Collectors.toList());
        int count = skuMapper.insertList(skuList);
        if (count != skuList.size()) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }

    /**
     * 修改上下架的问题
     * @param spuId
     * @param saleable
     */
    @Override
    @Transactional
    public void updateSaleable(Long spuId, Boolean saleable) {

        //将spuid为这个的修改成指定的值
        Spu spu = new Spu();
        spu.setId(spuId);
        spu.setSaleable(saleable);
        int i = spuMapper.updateByPrimaryKeySelective(spu);
        if (i != 1) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        //将spu 更改之后 下面的所有sku都要更改
        Example example = new Example(Sku.class);

        example.createCriteria().andEqualTo("spuId",spuId);

        Sku sku = new Sku();
        sku.setEnable(saleable);
        int i1 = skuMapper.updateByExampleSelective(sku, example);

        List<Sku> skus = skuMapper.selectByExample(example);
        if (i1!=skus.size()){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }

        //这里 商品上下架 导致搜索 和静态页的服务的创建和删除

        //发送消息
        String item_key = saleable ? MQConstants.RoutingKey.ITEM_UP_KEY : MQConstants.RoutingKey.ITEM_DOWN_KEY;
        amqpTemplate.convertAndSend(MQConstants.Exchange.ITEM_EXCHANGE_NAME,item_key,spuId);

    }

    /**
     * 根据spuid 查询 spudatil的信息
     * @param spuId
     * @return
     */
    @Override
    public SpuDetailDTO queryDatailBySpId(Long spuId) {

        SpuDetail spuDetail = new SpuDetail();
        spuDetail.setSpuId(spuId);
        SpuDetail spuDetail1 = spuDetailMapper.selectByPrimaryKey(spuDetail);
        if (spuDetail1 == null){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }

        return BeanHelper.copyProperties(spuDetail1,SpuDetailDTO.class);
    }

    /**
     * 根据spuid 查询spu的信息
     * @param spuId
     * @return
     */
    @Override
    public SpuDTO querySpuBySpId(Long spuId) {

        Spu spu1 = spuMapper.selectByPrimaryKey(spuId);
        if (null==spu1) {
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        SpuDTO spuDTO = BeanHelper.copyProperties(spu1, SpuDTO.class);
        //查询spudatil
        spuDTO.setSpuDetail(queryDatailBySpId(spuId));
        //查询skus
        spuDTO.setSkus(querySkuBySpuId(spuId));

        return spuDTO;
    }

    /**
     * 根据spuid 查询skulist
     * @param spuId
     * @return
     */
    @Override
    public List<SkuDTO> querySkuBySpuId(Long spuId) {

        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> select = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(select)){
            throw  new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }

        return BeanHelper.copyWithCollection(select,SkuDTO.class);
    }
}
