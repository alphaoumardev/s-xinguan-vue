package com.qkm.xinguan.exception;

import com.qkm.xinguan.response.ResultCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author qiukangming
 * @version 1.0
 * @description 业务异常类
 * @since 2020/09/24 14:47
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {
    /**
     * 状态码
     */
    @ApiModelProperty(value = "状态码")
    private Integer code;

    /**
     * 错误消息
     */
    @ApiModelProperty(value = "错误消息")
    private String errMsg;

    /**
     * 构造函数
     *
     * @param resultCode 状态码
     */
    public BusinessException(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.errMsg = resultCode.getMessage();
    }

    public BusinessException(ResultCode resultCode, Exception e) {
        this.code = resultCode.getCode();
        this.errMsg = resultCode.getMessage() + e.getMessage();
    }

    public BusinessException(String msg) {
        this.code = ResultCode.FAILED.getCode();
        this.errMsg = msg;
    }

    public BusinessException(ResultCode resultCode, String errMsg) {
        this.code = resultCode.getCode();
        this.errMsg = errMsg;
    }
}
