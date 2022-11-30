package com.qkm.xinguan.controller;

import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.ConsumerVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author qiukangming
 * @version 1.0
 * @description 物资去向控制器接口
 * @since 2020/09/26 11:48
 */

@RequestMapping("/business/consumer")
@Api(value = "物资去向管理模块", tags = "物资去向管理接口")
public interface IConsumerController {

    @ApiOperation(value = "去向列表", notes = "去向列表,根据去向名模糊查询")
    @PreAuthorize("hasAuthority('consumer:query')")
    @GetMapping("/findConsumerList")
    Result findConsumerList(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size") Integer size, ConsumerVo consumerVo);

    @ApiOperation(value = "添加去向", notes = "添加物资去向")
    @PreAuthorize("hasAuthority('consumer:add')")
    @PostMapping("/add")
    Result add(@RequestBody @Validated ConsumerVo consumerVo, BindingResult result);

    @ApiOperation(value = "根据id获取一条数据", notes = "根据id获取一条数据")
    @PreAuthorize("hasAuthority('consumer:query')")
    @GetMapping("/findById/{id}")
    Result findById(@PathVariable Long id);

    @ApiOperation(value = "更新去向", notes = "更新去向信息")
    @PreAuthorize("hasAuthority('consumer:update')")
    @PutMapping("/update/{id}")
    Result update(@PathVariable Long id, @RequestBody @Validated ConsumerVo consumerVo, BindingResult result);

    @ApiOperation(value = "删除去向", notes = "删除去向信息")
    @PreAuthorize("hasAuthority('consumer:delete')")
    @DeleteMapping("/delete/{id}")
    Result delete(@PathVariable Long id);

    @ApiOperation(value = "所有去向", notes = "所有去向列表")
    @PreAuthorize("hasAuthority('consumer:query')")
    @GetMapping("/findAll")
    Result findAll();
}
