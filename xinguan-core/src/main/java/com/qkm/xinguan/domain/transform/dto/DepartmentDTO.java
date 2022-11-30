package com.qkm.xinguan.domain.transform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/01/25 14:12
 * @description 部门传输对象
 */

@Data
public class DepartmentDTO {
    private Long id;

    @NotBlank(message = "部门名称不能为空")
    private String name;

    @NotBlank(message = "部门联系电话不能为空")
    private String phone;

    @NotBlank(message = "部门地址不能为空")
    private String address;
}
