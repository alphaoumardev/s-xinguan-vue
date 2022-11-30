package com.qkm.xinguan.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/01/25 15:48
 * @description 部门导出对象
 */

@Data
public class DepartmentExportVo {

    @Excel(name = "部门ID", width = 25)
    private Long id;

    @Excel(name = "部门名称", width = 25)
    private String name;

    @Excel(name = "部门总人数", width = 25)
    private Integer total;

    @Excel(name = "部门联系电话", width = 25)
    private String phone;

    @Excel(name = "部门地址", width = 25)
    private String address;

    @Excel(name = "部门创建时间", width = 25)
    private LocalDateTime createTime;

    @Excel(name = "部门信息修改时间", width = 25)
    private LocalDateTime modifiedTime;
}
