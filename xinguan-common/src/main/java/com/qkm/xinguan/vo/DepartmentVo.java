package com.qkm.xinguan.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/01/24 21:46
 * @description 部门视图对象
 */

@Data
public class DepartmentVo {
    private Long id;
    private String name;
    private Integer total;
    private String phone;
    private String address;
    private LocalDateTime createTime;
    private LocalDateTime modifiedTime;
}
