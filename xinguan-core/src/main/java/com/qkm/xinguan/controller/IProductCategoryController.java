package com.qkm.xinguan.controller;

import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.ProductCategoryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author qiukangming
 * @version 1.0
 * @description
 * @since 2020/09/26 22:29
 */

@RequestMapping("/business/product-category")
@Api(value = "物资类别管理模块", tags = "物资类别管理接口")
public interface IProductCategoryController {

    @ApiOperation(value = "分类列表", notes = "物资分类列表,根据物资分类名模糊查询")
    @PreAuthorize("hasAuthority('productCategory:query')")
    @GetMapping("/findProductCategoryList")
    Result findProductCategoryList(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size, ProductCategoryVo productCategoryVo);

    @ApiOperation(value = "分类树形结构", notes = "获取分类树形结构")
    @PreAuthorize("hasAuthority('productCategory:queryTree')")
    @GetMapping("/categoryTree")
    Result categoryTree(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10000") Integer size);

    @ApiOperation(value = "父级分类树", notes = "获取父级分类树")
    @PreAuthorize("hasAuthority('productCategory:queryParentTree')")
    @GetMapping("/getParentCategoryTree")
    Result getParentCategoryTree();

    @ApiOperation(value = "所有分类", notes = "查询所有分类")
    @PreAuthorize("hasAuthority('productCategory:query')")
    @GetMapping("/findAll")
    Result findAll();

    @ApiOperation(value = "添加分类", notes = "添加物资分类信息")
    @PreAuthorize("hasAuthority('productCategory:add')")
    @PostMapping("/add")
    Result add(@RequestBody @Validated ProductCategoryVo productCategoryVo);

    @ApiOperation(value = "根据ID获取分类信息", notes = "根据ID获取分类信息")
    @PreAuthorize("hasAuthority('productCategory:query')")
    @GetMapping("/findById/{id}")
    Result findById(@PathVariable("id") Long id);

    @ApiOperation(value = "更新分类", notes = "更新分类信息")
    @PreAuthorize("hasAuthority('productCategory:update')")
    @PutMapping("/update/{id}")
    Result update(@PathVariable("id") Long id, @RequestBody @Validated ProductCategoryVo productCategoryVo);

    @ApiOperation(value = "删除分类", notes = "删除分类信息")
    @PreAuthorize("hasAuthority('productCategory:delete')")
    @DeleteMapping("/delete/{id}")
    Result delete(@PathVariable("id") Long id);
}
