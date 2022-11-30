package com.qkm.xinguan.domain.transform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/03/31 21:04
 * @description
 */

@Data
public class HealthDTO {
    private Long id;

    @NotBlank(message = "地址不能为空")
    private String address;

    private Long userId;

    @NotNull(message = "当前情况不能为空")
    private Integer situation;

    @NotNull(message = "是否接触不能为空")
    private Integer touch;

    @NotNull(message = "是否路过不能为空")
    private Integer passby;

    @NotNull(message = "是否招待不能为空")
    private Integer reception;

    private LocalDateTime createTime;
}
