package com.qkm.xinguan.controller;

import com.qkm.xinguan.domain.transform.form.LogSearchForm;
import com.qkm.xinguan.response.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author qiukangming
 * @version 1.0
 * @description
 * @since 2020/09/26 22:21
 */

@RequestMapping("/system/operator/log")
@Api(value = "系统操作日志管理模块", tags = "系统操作日志管理接口")
public interface ILogController {

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('log:query')")
    @ApiOperation(value = "获取系统操作日志", notes = "系统操作日志")
    Result getSystemLogList(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size, LogSearchForm logSearchForm);

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('log:delete')")
    @ApiOperation(value = "根据ID删除操作日志", notes = "删除操作日志")
    Result delById(@PathVariable("id") Long id);

    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('log:batchDelete')")
    @ApiOperation(value = "批量删除操作日志", notes = "批量删除操作日志")
    Result batchDelById(@RequestBody Long[] ids);

}
