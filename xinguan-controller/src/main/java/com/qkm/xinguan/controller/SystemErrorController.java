package com.qkm.xinguan.controller;

import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/10 21:59
 * @description
 */
@RestController
@Api(value = "系统异常捕获接口", tags = "系统异常")
public class SystemErrorController implements ErrorController {

    @GetMapping("/error")
    @ApiOperation(value = "系统异常捕获",notes = "系统异常捕获")
    public Result handleError(HttpServletRequest request){
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        ResultCode resultCode;
        if(statusCode == 404){
            resultCode = ResultCode.NOT_FOUND;
        }else if(statusCode == 400){
            resultCode = ResultCode.FAILED;
        }else if(statusCode == 405){
            resultCode = ResultCode.METHOD_NOT_SUPPERED;
        }else if(statusCode == 500){
            resultCode = ResultCode.SYSTEM_ERROR;
        }else {
            resultCode = ResultCode.UNKNOW_ERROR;
        }
        return Result.error(resultCode);

    }

    /**
     * 返回错误路径
     *
     * @return String
     */
    @Override
    public String getErrorPath() {
        return "/error";
    }
}
