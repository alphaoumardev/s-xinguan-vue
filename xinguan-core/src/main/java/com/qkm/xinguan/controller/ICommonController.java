package com.qkm.xinguan.controller;

import com.qkm.xinguan.response.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/03/29 16:55
 * @description
 */

@RequestMapping("/system/common/")
@Api(value = "公共接口模块", tags = "公共接口接口")
public interface ICommonController {

    @GetMapping("/druid")
    @PreAuthorize("hasAuthority('common:druid')")
    @ApiOperation(value = "获取 sql 监控登录用户名和密码", notes = "sql 监控登录用户名和密码")
    Result getDruidLoginConfig();

    @GetMapping("/sysinfo")
    @ApiOperation(value = "获取系统的联系我们信息", notes = "系统的联系我们信息")
    Result getSystemAuthorInfo();
}
