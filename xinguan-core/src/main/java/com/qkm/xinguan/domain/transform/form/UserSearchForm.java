package com.qkm.xinguan.domain.transform.form;

import lombok.Data;

/**
 * @author qiukangming
 * @version 1.0
 * @description 用户条件查询表单
 * @since 2020/09/25 14:23
 */

@Data
public class UserSearchForm {
    private Integer departmentId;
    private String username;
    private String email;
    private Integer sex;
    private String nikeName;
    private String forbidden;
}
