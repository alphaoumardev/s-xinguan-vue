package com.qkm.xinguan.controller;

import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.OutStockVo;
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
 * @since 2020/09/26 22:27
 */

@RequestMapping("/business/out-stock")
@Api(value = "物资出库管理模块", tags = "物资出库相关接口")
public interface IOutStockController {

    @ApiOperation(value = "提交发放单", notes = "提交发放单信息")
    @PreAuthorize("hasAuthority('outStock:add')")
    @PostMapping("/addOutStock")
    Result addOutStock(@RequestBody @Validated OutStockVo outStockVo, BindingResult result);

    @ApiOperation(value = "出库单列表", notes = "分页查询出库单列表")
    @PreAuthorize("hasAuthority('outStock:query')")
    @GetMapping("/findOutStockList")
    Result findInStockList(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size") Integer size, OutStockVo outStockVo);

    @ApiOperation(value = "移入回收站", notes = "移入回收站")
    @PreAuthorize("hasAuthority('outStock:remove')")
    @PutMapping("/remove/{id}")
    Result remove(@PathVariable("id") Long id);

    @ApiOperation(value = "发放单明细", notes = "发放明细")
    @PreAuthorize("hasAuthority('outStock:detail')")
    @GetMapping("/detail/{id}")
    Result detail(@PathVariable("id") Long id, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size",defaultValue = "3") Integer size);

    @ApiOperation(value = "删除物资发放单", notes = "删除发放单")
    @PreAuthorize("hasAuthority('outStock:delete')")
    @DeleteMapping("/delete/{id}")
    Result delete(@PathVariable("id") Long id);

    @ApiOperation(value = "入库审核", notes = "发放单入库审核")
    @PreAuthorize("hasAuthority('outStock:publish')")
    @PutMapping("/publish/{id}")
    Result publish(@PathVariable("id") Long id);

    @ApiOperation(value = "恢复数据", notes = "从回收站中恢复入库单")
    @PreAuthorize("hasAuthority('outStock:back')")
    @PutMapping("/back/{id}")
    Result back(@PathVariable("id") Long id);
}
