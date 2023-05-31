package com.github.gelald.oauth2.advice;

import com.github.gelald.oauth2.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception exception) throws Exception {
        //授权码模式下，不能自己处理这个异常，否则无法获取授权码，会抛出这个异常
        //org.springframework.security.authentication.InsufficientAuthenticationException: User must be authenticated with Spring Security before authorization can be completed.
        //https://blog.csdn.net/honor_zhang/article/details/119564265
        if ("org.springframework.security.authentication.InsufficientAuthenticationException".equals(exception.getClass().getName())) {
            throw exception;
        }
        log.error("发生异常: ", exception);
        return Result.failed(exception.getMessage());
    }
}
