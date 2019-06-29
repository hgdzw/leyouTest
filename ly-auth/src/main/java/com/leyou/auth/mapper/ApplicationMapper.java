package com.leyou.auth.mapper;

import com.leyou.auth.entity.ApplicationInfo;
import com.leyou.common.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: dzw
 * @Date: 2019/6/29 11:48
 * @Version 1.0
 */
public interface ApplicationMapper extends BaseMapper<ApplicationInfo> {

    List<Long> queryTargetById(@Param("id") Long id);
}
