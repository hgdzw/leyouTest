package com.leyou.common.exception;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Getter;

/**
 * @Author: dzw
 *
 * 这个类继承runtimeexception  就是想可以返回状态码和状态信息回去
 * @Date: 2019/6/12 14:03
 * @Version 1.0
 */
@Getter
public class LyException extends RuntimeException {

    private int status;

    public LyException(ExceptionEnum em) {
        super(em.getMessage());
        this.status = em.getStatus();
    }
    public LyException(ExceptionEnum em,Throwable cause){
        super(em.getMessage(),cause);
        this.status=em.getStatus();
    }

}
