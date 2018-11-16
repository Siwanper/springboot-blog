package com.swp.springboot.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 描述:
 * 日志
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-27 10:55 AM
 */

@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut(value = "execution(public * com.swp.springboot.controller..*.*(..))")
    public void weblog(){

    }

    @Before(value = "weblog()")
    public void doBefore(JoinPoint joinPoint){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();


        logger.info(
                "URL : " + request.getRequestURI().toString() + ", IP : " +
                        request.getRemoteAddr() + ", CLASS_METHOD : " +
                joinPoint.getSignature().getDeclaringTypeName() + "." +
                joinPoint.getSignature().getName() + ", ARGS : " +
                Arrays.asList(joinPoint.getArgs())
        );
    }

    @AfterReturning(returning = "object", pointcut = "weblog()")
    public void doAfter(Object object){
        logger.info("RESPONSE : " + object);
    }

}
