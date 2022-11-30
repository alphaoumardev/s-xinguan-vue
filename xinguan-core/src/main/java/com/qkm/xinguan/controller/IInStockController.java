package com.qkm.xinguan.controller;

import com.qkm.xinguan.domain.transform.form.InStockSearchForm;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.InStockVo;
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
 * @since 2020/09/26 22:11
 */

@RequestMapping("/business/in-stock")
@Api(value = "入库记录管理模块", tags = "入库记录管理接口")
public interface IInStockController {

    @GetMapping("/selectList")
    @PreAuthorize("hasAuthority('inStock:query')")
    @ApiOperation(value = "查询入库记录", notes = "查询入库记录")
    Result getInStockList(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size, InStockSearchForm inStockSearchForm);

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('inStock:export')")
    @ApiOperation(value = "导出入库记录列表", notes = "入库记录列表导出")
    void exportInStockList(HttpServletResponse response, InStockSearchForm searchForm);

    @GetMapping("/detail/{id}")
    @PreAuthorize("hasAuthority('inStock:detail')")
    @ApiOperation(value = "查询入库记录明细页面", notes = "入库记录明细页面")
    Result getInStockDetailList(@PathVariable("id") Long id, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "5") Integer size);

    @PutMapping("/remove/{id}")
    @PreAuthorize("hasAuthority('inStock:remove')")
    @ApiOperation(value = "移入回收站", notes = "移入回收站")
    Result remove(@PathVariable("id") Long id);

    @PutMapping("/back/{id}")
    @PreAuthorize("hasAuthority('inStock:back')")
    @ApiOperation(value = "回收站恢复数据", notes = "回收站恢复")
    Result back(@PathVariable("id") Long id);

    @PutMapping("/publish/{id}")
    @PreAuthorize("hasAuthority('inStock:publish')")
    @ApiOperation(value = "入库审核", notes = "入库审核")
    Result publish(@PathVariable("id") Long id);

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('inStock:delete')")
    @ApiOperation(value = "删除物资入库单", notes = "删除物资入库单")
    Result delete(@PathVariable("id") Long id);

    @PostMapping("/addInStock")
    @PreAuthorize("hasAuthority('inStock:add')")
    @ApiOperation(value = "添加物资入库", notes = "添加物资入库")
    Result addInStock(@RequestBody @Validated InStockVo inStockVo);
}
