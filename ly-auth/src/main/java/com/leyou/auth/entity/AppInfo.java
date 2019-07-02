package com.leyou.auth.entity;

import lombok.Data;

import java.util.List;

/**
 * @author 虎哥
 */
@Data
public class AppInfo {
    private Long id;
    private String serviceName;
    private List<Long> targetList;
}