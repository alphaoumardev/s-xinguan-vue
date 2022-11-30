package com.qkm.xinguan.controller;

import com.qkm.xinguan.domain.transform.form.ImageSearchForm;
import com.qkm.xinguan.response.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author qiukangming
 * @version 1.0
 * @description 阿里OSS对象存储接口
 * @since 2020/10/13 16:36
 */

@RequestMapping("/system/oss")
@Api(value = "OSS文件对象存储模块", tags = "OSS对象存储接口")
public interface IOssFileController {

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('oss:upload')")
    @ApiOperation(value = "上传文件", notes = "文件上传")
    Result headUpLoad(MultipartFile file) throws Exception;

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('oss:delete')")
    @ApiOperation(value = "删除文件", notes = "删除文件")
    Result deleteFile(@RequestParam("fileName") String fileName);

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('oss:query')")
    @ApiOperation(value = "获取文件列表", notes = "获取文件列表")
    Result listFile(@RequestParam(value = "page", defaultValue = "1") Integer page,
                    @RequestParam(value = "size", defaultValue = "5") Integer size,
                    @RequestBody ImageSearchForm imageSearchForm);
}
