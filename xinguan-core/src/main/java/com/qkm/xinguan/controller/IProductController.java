package com.qkm.xinguan.controller;

import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.ProductVo;
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
 * @since 2020/09/26 22:30
 */

@RequestMapping("/business/product")
@Api(value = "物资资料管理模块", tags = "物资资料管理接口")
public interface IProductController {

    @ApiOperation(value = "获取物资列表", notes = "物资列表,根据物资名模糊查询")
    @PreAuthorize("hasAuthority('product:query')")
    @GetMapping("/findProductList")
    Result findProductList(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size, @RequestParam(value = "categorys", required = false) String categorys, @RequestParam(value = "filter", defaultValue = "false") Boolean filter ,ProductVo productVo);

    @ApiOperation(value = "库存列表", notes = "库存列表,根据物资名模糊查询")
    @PreAuthorize("hasAuthority('product:queryStocks')")
    @GetMapping("/findProductStocks")
    Result findProductStocks(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size, @RequestParam(value = "categorys", required = false) String categorys ,ProductVo productVo);

    @ApiOperation(value = "获取所有库存列表", notes = "所有库存列表(饼图使用)")
    @PreAuthorize("hasAuthority('product:queryAllStocks')")
    @GetMapping("/findAllProductStocks")
    Result findAllProductStocks(String categorys, ProductVo productVo);

    @ApiOperation(value = "添加物资", notes = "添加物资")
    @PreAuthorize("hasAuthority('product:add')")
    @PostMapping("/add")
    Result add(@RequestBody @Validated ProductVo productVo, BindingResult result);

    @ApiOperation(value = "根据ID获取物资", notes = "根据ID获取物资,")
    @PreAuthorize("hasAuthority('product:query')")
    @GetMapping("/findById/{id}")
    Result findById(@PathVariable("id") Long id);

    @ApiOperation(value = "更新物资", notes = "更新物资,")
    @PreAuthorize("hasAuthority('product:update')")
    @PostMapping("/update/{id}")
    Result update(@PathVariable("id") Long id, @RequestBody @Validated ProductVo productVo, BindingResult result);

    @ApiOperation(value = "删除物资", notes = "删除物资,")
    @PreAuthorize("hasAuthority('product:delete')")
    @DeleteMapping("/delete/{id}")
    Result delete(@PathVariable("id") Long id);

    @ApiOperation(value = "移入回收站", notes = "移入回收站,")
    @PreAuthorize("hasAuthority('product:remove')")
    @PutMapping("/remove/{id}")
    Result remove(@PathVariable("id") Long id);

    @ApiOperation(value = "物资添加审核", notes = "物资添加审核")
    @PreAuthorize("hasAuthority('product:publish')")
    @PutMapping("/publish/{id}")
    Result publish(@PathVariable("id") Long id);

    @ApiOperation(value = "恢复物资", notes = "从回收站中恢复物资")
    @PreAuthorize("hasAuthority('product:back')")
    @PutMapping("/back/{id}")
    Result back(@PathVariable("id") Long id);
}
