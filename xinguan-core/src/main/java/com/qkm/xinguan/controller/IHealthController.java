package com.qkm.xinguan.controller;

import com.qkm.xinguan.domain.transform.dto.HealthDTO;
import com.qkm.xinguan.domain.transform.form.PointSearchForm;
import com.qkm.xinguan.response.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author qiukangming
 * @version 1.0
 * @description
 * @since 2020/09/26 22:08
 */

@RequestMapping("/health")
@Api(value = "健康疫情管理模块", tags = "健康疫情管理接口")
public interface IHealthController {

    @GetMapping("/yq/data")
    @ApiOperation(value = "获取疫情数据，通过开课吧提供的接口", notes = "下载疫情数据")
    Result getYqData();

    @GetMapping("/isReport")
    @ApiOperation(value = "是否打卡",notes = "今日是否打卡")
    Result isReport();

    @PostMapping("/report")
    @PreAuthorize("hasAuthority('health:report')")
    @ApiOperation(value = "健康上报",notes = "用户健康上报")
    Result report(@RequestBody @Validated HealthDTO healthDTO);

    @GetMapping("/history")
    @ApiOperation(value = "健康上报记录",notes = "用户健康上报记录")
    Result historyList(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size);

    @GetMapping("/points")
    @PreAuthorize("hasAuthority('health:query')")
    @ApiOperation(value = "查询打卡记录列表",notes = "打卡记录列表")
    Result getPointsList(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "20") Integer size, PointSearchForm pointSearchForm);

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('health:delete')")
    @ApiOperation(value = "删除打卡记录",notes = "删除记录")
    Result deletePoint(@PathVariable("id") Long id);

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('health:export')")
    @ApiOperation(value = "导出打卡记录列表", notes = "打卡记录列表导出")
    void exportPointsList(HttpServletResponse response, PointSearchForm pointSearchForm);
}
