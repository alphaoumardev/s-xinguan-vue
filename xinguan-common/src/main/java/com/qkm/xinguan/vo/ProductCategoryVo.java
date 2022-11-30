package com.qkm.xinguan.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/04 11:24
 * @description
 */

@Data
public class ProductCategoryVo {
    private Long id;

    @NotBlank(message = "类目名称不能为空")
    private String name;

    @NotBlank(message = "类目备注不能为空")
    private String remark;

    @NotNull(message = "排序号不能为空")
    private Integer sort;

    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;

    /** 父级分类id*/
    @NotNull(message = "父级菜单不能为空")
    private Long pid;
}
