package com.qkm.xinguan.domain.transform.dto;

import com.qkm.xinguan.domain.infrastructure.validation.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/01/28 14:54
 * @description 角色传输对象
 */

@Data
public class RoleDTO {
    private Long id;

    @NotBlank(message = "权限标识不能为空")
    private String roleCode;

    @NotBlank(message = "权限名称不能为空")
    private String roleName;

    private String remark;

    @NotNull(message = "角色状态不能为空")
    @EnumValue(intValues = {0, 1})
    private Integer forbidden;
}
