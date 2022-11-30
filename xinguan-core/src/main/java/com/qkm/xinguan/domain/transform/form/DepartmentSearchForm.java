package com.qkm.xinguan.domain.transform.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/01/23 22:36
 * @description
 */

@Data
public class DepartmentSearchForm {

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
     * 查询部门名称，支持模糊查询
     */
    private String deptName;

    /**
     * 查询部门电话号码，支持模糊查询
     */
    private String phone;
}
