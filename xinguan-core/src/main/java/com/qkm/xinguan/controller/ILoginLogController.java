package com.qkm.xinguan.controller;

import com.qkm.xinguan.domain.transform.form.LoginLogSearchForm;
import com.qkm.xinguan.response.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author qiukangming
 * @version 1.0
 * @description
 * @since 2020/09/26 22:24
 */

@RequestMapping("/system/login-log")
@Api(value = "登入日志管理模块", tags = "登入日志管理接口")
public interface ILoginLogController {

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('loginlog:query')")
    @ApiOperation(value = "查询登录日志列表信息", notes = "分页查询")
    Result searchLoginLogListPage(@RequestBody LoginLogSearchForm loginLogSearchForm);

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('loginlog:delete')")
    @ApiOperation(value = "删除登录日志", notes = "删除登录日志")
    Result deleteLoginLog(@PathVariable("id") Long id);

    @DeleteMapping("/batch/{ids}")
    @PreAuthorize("hasAuthority('loginlog:batchDelete')")
    @ApiOperation(value = "批量删除登录日志", notes = "批量删除登录日志")
    Result batchDeleteLoginLog(@PathVariable("ids") String ids);

    @GetMapping("/loginReport/{username}")
    @ApiOperation(value = "登入报表",notes = "用户登入报表")
    Result loginReport(@PathVariable("username") String username);
}
