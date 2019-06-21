package com.leyou.common.advice;

import com.leyou.common.exception.LyException;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 * @Author: dzw
 * 定义 异常的返回类
 * @Date: 2019/6/12 14:24
 * @Version 1.0
 */
@Getter
public class ExceptionResult {

    private int status;
    private String massage;
    private String timestamp;

    /**
     * 传递异常进来 生成结果
     * @param e
     */
    public ExceptionResult(LyException e) {
        this.status = e.getStatus();
        this.massage= e.getMessage();
        this.timestamp = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
    }
}
