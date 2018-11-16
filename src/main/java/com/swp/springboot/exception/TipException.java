package com.swp.springboot.exception;

/**
 * 描述:
 * 自定义服务器处理异常
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-25 3:15 PM
 */
public class TipException extends RuntimeException {

    public TipException() {
    }

    public TipException(String message) {
        super(message);
    }

    public TipException(String message, Throwable cause) {
        super(message, cause);
    }

    public TipException(Throwable cause) {
        super(cause);
    }
}
