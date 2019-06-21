package com.leyou.service;

import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;

import java.util.List;

/**
 * @Author: dzw
 * @Date: 2019/6/16 9:06
 * @Version 1.0
 */
public interface SpecService {

    List<SpecGroupDTO> queryGroupByCid(Long cid);

//    List<SpecParamDTO> queryParamByGid(Long gid);

    void saveParam(SpecParamDTO specParamDTO);

    void deleteParam(Long id);

    List<SpecParamDTO> querySpecParams(Long gid, Long cid, Boolean searching);
}
