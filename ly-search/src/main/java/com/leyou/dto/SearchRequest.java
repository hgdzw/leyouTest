package com.leyou.dto;

/**
 * @Author: dzw
 * @Date: 2019/6/20 11:22
 * @Version 1.0
 */
public class SearchRequest {

    private String key;
    private Integer page;

    private final static Integer DEFAULT_SIZE = 20; //大小不是从页面获取的 而是固定的
    private final static Integer DEFAULT_PAGE = 1;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        //页码不能小于一
        if(page == null){
            return DEFAULT_PAGE;
        }
        // 获取页码时做一些校验，不能小于1
        return Math.max(DEFAULT_PAGE, page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize(){
        return DEFAULT_SIZE;
    }
}
