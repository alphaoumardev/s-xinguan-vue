package com.qkm.xinguan.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author qiukangming
 * @version 1.0
 * @description 用户前端视图对象
 * @since 2020/09/25 14:25
 */

@Data
public class UserVo {
    private Long id;
    private String avatar;
    private String username;
    private String nickname;
    private String sex;
    private String deptName;
    private LocalDate birth;
    private String email;
    private String phoneNumber;
    private String roles;
    private Boolean forbidden;
}
