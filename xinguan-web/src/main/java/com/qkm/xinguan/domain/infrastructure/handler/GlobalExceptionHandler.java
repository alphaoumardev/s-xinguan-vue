package com.qkm.xinguan.domain.infrastructure.handler;

import com.qkm.xinguan.exception.BindingResultException;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiukangming
 * @version 1.0
 * @description 全局异常处理
 * @since 2020/09/24 12:21
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 算数异常
     *
     * @param request 请求
     * @param e       异常对象
     * @return Result
     */
    @ExceptionHandler(ArithmeticException.class)
    public Result error(HttpServletRequest request, ArithmeticException e) {
        log.error("全局异常[ArithmeticException]处理: [api: {}], 原因: {}", request.getRequestURL(), e.getMessage());
        return Result.error(ResultCode.ARITHMETIC);
    }

    /**
     * 业务异常信息捕获
     *
     * @param request 一次请求
     * @param e       异常对象
     * @return Result
     */
    @ExceptionHandler(BusinessException.class)
    public Result error(HttpServletRequest request, BusinessException e) {
        log.error("全局异常[BusinessException]处理: [api: {}], 原因: {}", request.getRequestURL(), e.getErrMsg());
        return Result.error().code(e.getCode()).message(e.getErrMsg());
    }

    /**
     * DTO对象绑定异常
     *
     * @param request 请求
     * @param e       异常对象
     * @return Result
     */
    @ExceptionHandler(BindingResultException.class)
    public Result error(HttpServletRequest request, BindingResultException e) {
        log.error("全局异常[BindingResultException]处理: [api: {}], 原因: {}", request.getRequestURL(), e.getErrMsg());
        return Result.error().code(e.getCode()).message(e.getErrMsg());
    }
}
