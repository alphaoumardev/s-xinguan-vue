package com.qkm.xinguan.controller;

import com.qkm.xinguan.domain.transform.dto.MenuDTO;
import com.qkm.xinguan.domain.transform.form.MenuSearchForm;
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
 * @description 菜单相关接口
 * @since 2020/09/26 22:25
 */

@RequestMapping("/system/menu")
@Api(value = "菜单管理模块", tags = "菜单管理接口")
public interface IMenuController {

    /**
     * 获取菜单和按钮
     *
     * @return Result
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('menu:query')")
    @ApiOperation(value = "查询菜单列表，展开数据", notes = "从菜单表中查询菜单列表")
    Result getMenuAndButton();

    /**
     * 获取菜单
     *
     * @return Result
     */
    @GetMapping("/menus")
    @ApiOperation(value = "查询菜单列表", notes = "从菜单表中查询菜单列表")
    Result getMenus();

    /**
     * 编辑，修改菜单
     *
     * @return Result
     */
    @PutMapping
    @PreAuthorize("hasAuthority('menu:update')")
    @ApiOperation(value = "菜单编辑", notes = "编辑修改菜单")
    Result modifyMenu(@RequestBody @Validated MenuDTO menuDTO, BindingResult result);

    /**
     * 添加菜单或者按钮
     *
     * @return Result
     */
    @PostMapping
    @PreAuthorize("hasAuthority('menu:add')")
    @ApiOperation(value = "菜单/按钮添加", notes = "添加菜单")
    Result submitMenu(@RequestBody @Validated MenuDTO menuDTO, BindingResult result);

    /**
     * 删除菜单/按钮
     *
     * @param id 菜单/按钮 ID
     * @return Result
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('menu:delete')")
    @ApiOperation(value = "菜单/按钮删除", notes = "删除菜单")
    Result deleteMenu(@PathVariable("id") Long id);

    /**
     * 导出菜单列表
     *
     * @param response 响应头
     * @param menuSearchForm 查询实体数据包
     */
    @PostMapping("/export")
    @PreAuthorize("hasAuthority('menu:export')")
    @ApiOperation(value = "导出菜单列表", notes = "菜单列表导出")
    void exportMenuList(HttpServletResponse response, @RequestBody MenuSearchForm menuSearchForm);
}
