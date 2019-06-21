package com.leyou.common.vo;

import lombok.Data;

import java.util.List;


/**
 * @Author: dzw
 * 这个是用来封装返回的分页结果的
 * @Date: 2019/6/12 17:37
 * @Version 1.0
 */
@Data
public class PageResult<T> {

    private long total; //总条数
    private Integer totalPage;  //总页数
    private List<T> items;


    public PageResult() {
    }

    public PageResult(long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public PageResult(long total, Integer totalPage, List<T> items) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
    }
}
