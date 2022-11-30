package com.qkm.xinguan.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/03/06 20:30
 * @description 菜单导出对象
 */

@Data
public class MenuExportVo {

    @Excel(name = "菜单资源ID", width = 25)
    private Long id;

    @Excel(name = "菜单父节点名称", width = 25)
    private String parentName;

    @Excel(name = "菜单名称", width = 25)
    private String menuName;

    @Excel(name = "菜单访问路径", width = 25)
    private String url;

    @Excel(name = "菜单权限标识", width = 25)
    private String perms;

    @Excel(name = "菜单图标", width = 25)
    private String icon;

    @Excel(name = "资源类型", width = 25)
    private String type;

    @Excel(name = "资源创建时间", width = 25)
    private LocalDateTime createTime;

    @Excel(name = "资源修改时间", width = 25)
    private LocalDateTime modifiedTime;

    @Excel(name = "菜单/资源状态", width = 25)
    private String available;

    @Excel(name = "菜单是否展开", width = 25) // 0:不展开，1：展开
    private String open;
}
