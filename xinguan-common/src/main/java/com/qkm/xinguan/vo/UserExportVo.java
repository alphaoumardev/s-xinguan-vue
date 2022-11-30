package com.qkm.xinguan.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/01/17 21:49
 * @description 用户导出对象
 */

@Data
public class UserExportVo {

    @Excel(name = "用户ID", width = 25)
    private Long id;

    @Excel(name = "用户头像地址", width = 25)
    private String avatar;

    @Excel(name = "用户名", width = 25)
    private String username;

    @Excel(name = "用户昵称", width = 25)
    private String nickname;

    @Excel(name = "用户性别", width = 25)
    private String sex;

    @Excel(name = "所在部门", width = 25)
    private String deptName;

    @Excel(name = "生日", width = 25)
    private LocalDate birth;

    @Excel(name = "邮箱地址", width = 25)
    private String email;

    @Excel(name = "手机号码", width = 25)
    private String phoneNumber;

    @Excel(name = "用户状态", width = 25)
    private String forbidden;
}
