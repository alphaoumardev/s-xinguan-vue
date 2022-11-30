package com.qkm.xinguan.controller;

import com.qkm.xinguan.domain.transform.dto.DepartmentDTO;
import com.qkm.xinguan.domain.transform.form.DepartmentSearchForm;
import com.qkm.xinguan.response.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

/**
 * @author qiukangming
 * @version 1.0
 * @description 部门信息
 * @since 2020/09/26 22:06
 */

@RequestMapping("/system/department")
@Api(value = "部门管理模块", tags = "部门管理接口")
public interface IDepartmentController {

    @GetMapping("/selectList")
    @PreAuthorize("hasAuthority('department:querySelect')")
    @ApiOperation(value = "查询部门名字以及人数", notes = "从部门表中分组查询用户人数")
    Result getSelectList();

    @GetMapping("/{deptName}")
    @PreAuthorize("hasAuthority('department:query')")
    @ApiOperation(value = "根据部门名字查询部门id", notes = "根据部门名称查询部门ID")
    Result getIdByName(@PathVariable("deptName") String deptName);

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('department:query')")
    @ApiOperation(value = "查询所有部门名称", notes = "查询部门列表信息")
    Result getList(@RequestBody @Validated DepartmentSearchForm searchForm, BindingResult result);

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('department:add')")
    @ApiOperation(value = "添加部门", notes = "添加部门信息")
    Result add(@RequestBody @Validated DepartmentDTO departmentDTO, BindingResult result);

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('department:update')")
    @ApiOperation(value = "修改部门信息", notes = "修改部门信息")
    Result update(@RequestBody @Validated DepartmentDTO departmentDTO, BindingResult result);

    @DeleteMapping("/del/{id}")
    @PreAuthorize("hasAuthority('department:delete')")
    @ApiOperation(value = "删除部门信息", notes = "删除部门信息")
    Result delete(@PathVariable("id") Long id);

    @PostMapping("/export")
    @PreAuthorize("hasAuthority('department:export')")
    @ApiOperation(value = "导出部门列表", notes = "部门列表导出")
    void exportUserList(HttpServletResponse response, @RequestBody @Validated DepartmentSearchForm searchForm, BindingResult result);
}
