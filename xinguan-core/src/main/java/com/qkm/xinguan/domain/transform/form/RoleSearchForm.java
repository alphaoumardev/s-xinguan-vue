package com.qkm.xinguan.domain.transform.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/01/28 14:47
 * @description 角色查询对象
 */

@Data
public class RoleSearchForm {
    /**
     * 查询第几页 默认第一页
     */
    @NotNull(message = "分页 page 不能为空")
    @Min(message = "page 不能小于 1", value = 1)
    private Integer page = 1;

    /**
     * 一页几条信息 默认10条
     */
    @NotNull(message = "分页 size 不能为空")
    @Min(message = "size 不能小于 1", value = 1)
    private Integer size = 10;

    /**
     * 查询角色名称
     */
    private String roleName;
}
