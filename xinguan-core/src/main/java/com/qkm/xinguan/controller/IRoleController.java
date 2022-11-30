package com.qkm.xinguan.controller;

import com.qkm.xinguan.domain.transform.dto.MenuIdsBodyDTO;
import com.qkm.xinguan.domain.transform.dto.RoleDTO;
import com.qkm.xinguan.domain.transform.form.RoleSearchForm;
import com.qkm.xinguan.response.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author qiukangming
 * @version 1.0
 * @description
 * @since 2020/09/26 22:32
 */
@RequestMapping("/system/role")
@Api(value = "角色管理模块", tags = "角色管理接口")
public interface IRoleController {

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('role:query')")
    @ApiOperation(value = "分页查询角色信息", notes = "分页查询列表信息")
    Result getList(@RequestBody @Validated RoleSearchForm roleSearchForm, BindingResult result);

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('role:add')")
    @ApiOperation(value = "增加角色信息", notes = "增加角色信息")
    Result add(@RequestBody @Validated RoleDTO roleDTO, BindingResult result);

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    @ApiOperation(value = "删除角色信息", notes = "删除角色信息")
    Result delete(@PathVariable("id") Long id);

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('role:update')")
    @ApiOperation(value = "修改角色信息", notes = "修改角色信息")
    Result update(@RequestBody @Validated RoleDTO roleDTO, BindingResult result);

    @PostMapping("/export")
    @PreAuthorize("hasAuthority('role:export')")
    @ApiOperation(value = "导出角色列表", notes = "角色列表导出")
    void exportUserList(HttpServletResponse response, @RequestBody @Validated RoleSearchForm roleSearchForm, BindingResult result);

    @PutMapping("/forbidden")
    @PreAuthorize("hasAuthority('role:status')")
    @ApiOperation(value = "修改角色状态", notes = "修改角色状态")
    Result updateRoleStatus(@RequestParam("id") Long id, @RequestParam("forbidden") Boolean forbidden);

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('role:queryAll')")
    @ApiOperation(value = "获取所有角色列表", notes = "角色列表")
    Result getAllList();

    @GetMapping("/roleMenu/{id}")
    @PreAuthorize("hasAuthority('role:queryMenuList')")
    @ApiOperation(value = "获取角色所拥有的菜单列表", notes = "角色拥有的菜单列表")
    Result getMenuListByRoleId(@PathVariable("id") Long id);

    @PostMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('role:authority')")
    @ApiOperation(value = "角色授权菜单", notes = "角色授权菜单")
    Result assignMenus(@PathVariable("id") Long id, @RequestBody MenuIdsBodyDTO menuIds);
}
