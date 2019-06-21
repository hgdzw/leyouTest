package com.leyou.item.dto;

import lombok.Data;

/**
 * @Author: dzw
 * @Date: 2019/6/16 9:37
 * @Version 1.0
 */
@Data
public class SpecParamDTO {
    private Long id;
    private Long cid;
    private Long groupId;
    private String name;
    private Boolean numeric;
    private String unit;
    private Boolean generic;
    private Boolean searching;
    private String segments;
}
