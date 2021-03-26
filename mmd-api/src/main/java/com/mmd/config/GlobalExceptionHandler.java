package com.mmd.config;

import com.mmd.entity.GlobalResult;
import com.uranus.security.exception.StepSecurityException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/28 21:38
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {StepSecurityException.class})
    public GlobalResult error(StepSecurityException exception, HttpServletRequest req, HttpServletResponse response) {
        return GlobalResult.errorTokenMsg(exception.getMessage());
    }
}
