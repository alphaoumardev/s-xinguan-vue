package com.qkm.xinguan.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qiukangming
 * @version 1.0
 * @description 公共返回结果
 * @since 2020/09/24 11:14
 */

@Data
@SuppressWarnings("unused")
public class Result {
    @ApiModelProperty(value = "是否成功")
    private Boolean isSuccess;

    @ApiModelProperty(value = "返回状态码")
    private Integer code;

    @ApiModelProperty(value = "返回状态信息")
    private String message;

    @ApiModelProperty(value = "返回的数据")
    private Object data;

    /**
     * 里面的方法都是静态方法的
     * 达到保护我们的属性的作用
     */
    private Result() {
    }

    /**
     * 使用链式编程
     */
    public static Result ok() {
        Result result = new Result();
        result.setIsSuccess(true);
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData("");
        return result;
    }

    public static Result ok(ResultCode resultCode) {
        Result result = new Result();
        result.setIsSuccess(true);
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        result.setData("");
        return result;
    }

    public static Result error() {
        Result result = new Result();
        result.setIsSuccess(false);
        result.setCode(ResultCode.FAILED.getCode());
        result.setMessage(ResultCode.FAILED.getMessage());
        result.setData("");
        return result;
    }

    public static Result error(ResultCode resultCode) {
        Result result = new Result();
        result.setIsSuccess(false);
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        result.setData("");
        return result;
    }

    public static Result error(String errorMsg) {
        Result result = new Result();
        result.setIsSuccess(false);
        result.setCode(ResultCode.FAILED.getCode());
        result.setMessage(errorMsg);
        result.setData("");
        return result;
    }

    /**
     * 自定义返回成功与否
     *
     * @param success 是否成功
     * @return Result
     */
    public Result success(Boolean success) {
        this.setIsSuccess(success);
        return this;
    }

    public Result message(String message) {
        this.setMessage(message);
        return this;
    }

    public Result code(Integer code) {
        this.setCode(code);
        return this;
    }

    public Result data(Object obj) {
        this.setData(obj);
        return this;
    }
}
