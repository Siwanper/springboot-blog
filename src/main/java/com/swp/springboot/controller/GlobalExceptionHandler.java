package com.swp.springboot.controller;

import com.swp.springboot.exception.TipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 描述:
 * 统一异常处理类
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-25 3:05 PM
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public String exceptionHanlder(Exception e) {
        logger.error("find exception:e+{}",e.getMessage());
        return "common/error_404";
    }

    @ExceptionHandler(value = TipException.class)
    public String tipException(Exception e) {
        logger.error("find exception:e+{}",e.getMessage());
        return "common/error_505";
    }

}
