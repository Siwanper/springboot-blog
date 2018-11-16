package com.swp.springboot.controller.helper;

import com.swp.springboot.exception.TipException;
import com.swp.springboot.modal.bo.RestResponseBo;
import org.slf4j.Logger;

/**
 * 描述:
 * 统一异常处理类
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-26 10:07 AM
 */
public class ExceptionHelper {

    public static RestResponseBo handlerException(Logger logger, String msg, Exception exception) {
        if (exception instanceof TipException) {
            msg = exception.getMessage();
        } else {
            logger.error(msg, exception);
        }
        return RestResponseBo.fail(msg);
    }

}
