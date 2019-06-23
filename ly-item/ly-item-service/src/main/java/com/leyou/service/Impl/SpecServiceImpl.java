package com.leyou.service.Impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.entity.SpecGroup;
import com.leyou.entity.SpecParam;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.mapper.SpecGroupMapper;
import com.leyou.mapper.SpecParamMapper;
import com.leyou.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: dzw
 * @Date: 2019/6/16 9:07
 * @Version 1.0
 */
@Service
public class SpecServiceImpl implements SpecService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据cid 查询规格组信息
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroupDTO> queryGroupByCid(Long cid) {

        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> groups = specGroupMapper.select(specGroup);

        //判空
        if (CollectionUtils.isEmpty(groups)){

            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        List<SpecGroupDTO> specGroupDTOS = BeanHelper.copyWithCollection(groups, SpecGroupDTO.class);

        return specGroupDTOS;
    }

    /**
     * 根据 分类id 查询规格组 和 规格的信息
     *  和上面不一样的是  里面含有规格信息
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroupDTO> querySpecsByCid(Long cid) {

        //查询规格组
        List<SpecGroupDTO> groupDTOS = queryGroupByCid(cid);
        //查询规格参数
        List<SpecParamDTO> specParamDTOS = querySpecParams(null, cid, null);

        // 将规格参数按照groupId进行分组，得到每个group下的param的集合 TODO 这里分组是个问题
        Map<Long, List<SpecParamDTO>> collect = specParamDTOS.stream().collect(Collectors.groupingBy(SpecParamDTO::getGroupId));

        //填写到 group中
        for (SpecGroupDTO groupDTO : groupDTOS) {
            groupDTO.setParams(collect.get(groupDTO.getId()));
        }
        return groupDTOS;
    }

    /**
     * 根据组id 去查组内参数
     *  传三个参数进来 高级查询
     *
     * 待改造
     * @param gid
     * @return
     */
    @Override
    public List<SpecParamDTO> querySpecParams(Long gid, Long cid, Boolean searching) {


        //gid 和cid 一定先有一个
        if (null == gid && null ==cid){
            throw  new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        SpecParam param = new SpecParam();
        param.setCid(cid);
        param.setGroupId(gid);
        param.setSearching(searching);

        List<SpecParam> params = specParamMapper.select(param);
        if (CollectionUtils.isEmpty(params)){

            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }

        return BeanHelper.copyWithCollection(params, SpecParamDTO.class);


    }

    /**
     * 对规格参数的增加
     * @param specParamDTO
     */
    @Override
    public void saveParam(SpecParamDTO specParamDTO) {

        SpecParam specParam = BeanHelper.copyProperties(specParamDTO, SpecParam.class);

        int i = specParamMapper.insertSelective(specParam);
        //新增失败
        if (i!=1){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

    }

    /**
     * 根据id 删除规格参数
     * @param id
     */
    @Override
    public void deleteParam(Long id) {
        SpecParam specParam = new SpecParam();
        specParam.setId(id);
        int delete = specParamMapper.delete(specParam);
        if (1 != delete) {
            throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }

    }
}
