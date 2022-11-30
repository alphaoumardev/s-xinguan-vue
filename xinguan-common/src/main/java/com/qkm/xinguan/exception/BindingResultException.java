package com.qkm.xinguan.exception;

import com.qkm.xinguan.response.ResultCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiukangming
 * @version 1.0
 * @description
 * @since 2020/09/28 16:49
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BindingResultException extends RuntimeException {
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
     * @param result 绑定状态对象
     */
    public BindingResultException(BindingResult result) {
        List<String> errors = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        this.code = ResultCode.PARAM_ERROR.getCode();
        this.errMsg = ResultCode.PARAM_ERROR.getMessage() + "[ " + String.join(",", errors) + " ]";
    }
}
