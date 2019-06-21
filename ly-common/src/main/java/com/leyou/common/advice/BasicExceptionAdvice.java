package com.leyou.common.advice;

import com.leyou.common.exception.LyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 自定义全局异常
 * @Author: dzw
 * @Date: 2019/6/11 21:37
 * @Version 1.0
 */
@ControllerAdvice
@Slf4j
public class BasicExceptionAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handlerException(RuntimeException e){
        return ResponseEntity.status(500).body(e.getMessage());
    }


    @ExceptionHandler(LyException.class)
    public ResponseEntity<ExceptionResult> handler2Exception(LyException e){
        //返回的是一个异常结果类了

        return ResponseEntity.status(e.getStatus()).body(new ExceptionResult(e));
    }


}