package com.qkm.xinguan.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色菜单关联表
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_role_menu")
@ApiModel(value = "RoleMenu对象", description = "角色菜单关联表")
public class RoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "角色ID")
    private Long roleId;

    @ApiModelProperty(value = "菜单/按钮ID")
    private Long menuId;

    @ApiModelProperty(value = "菜单/按钮类型")
    private Long menuType;

}
