package com.qkm.xinguan.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/01/28 15:34
 * @description 角色信息导出对象
 */

@Data
public class RoleExportVo {

    @Excel(name = "角色ID", width = 25)
    private Long id;

    @Excel(name = "角色标识码", width = 25)
    private String roleCode;

    @Excel(name = "角色名称", width = 25)
    private String roleName;

    @Excel(name = "角色描述信息", width = 25)
    private String remark;

    @Excel(name = "创建角色信息时间", width = 25)
    private LocalDateTime createTime;

    @Excel(name = "修改角色信息时间", width = 25)
    private LocalDateTime modifiedTime;

    @Excel(name = "角色状态", width = 25)
    private String forbidden;
}
