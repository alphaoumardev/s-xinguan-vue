package com.qkm.xinguan.controller;

import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.SupplierVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @author qiukangming
 * @version 1.0
 * @description
 * @since 2020/09/26 22:35
 */

@RequestMapping("/business/supplier")
@Api(value = "物资来源管理模块", tags = "物资来源管理接口")
public interface ISupplierController {

    @ApiOperation(value = "所有来源", notes = "所有来源列表")
    @PreAuthorize("hasAuthority('supplier:queryAll')")
    @GetMapping("/findAll")
    Result findAll();

    @ApiOperation(value = "来源列表", notes = "来源列表,根据来源名模糊查询")
    @PreAuthorize("hasAuthority('supplier:query')")
    @GetMapping("/findSupplierList")
    Result findSupplierList(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size") Integer size, SupplierVo supplierVo);

    @ApiOperation(value = "添加来源", notes = "添加来源信心")
    @PreAuthorize("hasAuthority('supplier:add')")
    @PostMapping("/add")
    Result add(@RequestBody @Validated SupplierVo supplierVo, BindingResult result);

    @ApiOperation(value = "根据ID获取详细信息", notes = "根据ID获取详细信息")
    @PreAuthorize("hasAuthority('supplier:query')")
    @GetMapping("/findById/{id}")
    Result edit(@PathVariable("id") Long id);

    @ApiOperation(value = "更新来源", notes = "更新来源信息")
    @PreAuthorize("hasAuthority('supplier:update')")
    @PutMapping("/update/{id}")
    Result update(@PathVariable("id") Long id, @RequestBody @Validated SupplierVo supplierVo, BindingResult result);

    @ApiOperation(value = "删除来源", notes = "删除来源信息")
    @PreAuthorize("hasAuthority('supplier:delete')")
    @DeleteMapping("/delete/{id}")
    Result delete(@PathVariable("id") Long id);
}
