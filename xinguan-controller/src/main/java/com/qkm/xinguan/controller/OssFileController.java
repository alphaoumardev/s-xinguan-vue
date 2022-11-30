package com.qkm.xinguan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.infrastructure.annotation.ControllerEndpoint;
import com.qkm.xinguan.domain.transform.form.ImageSearchForm;
import com.qkm.xinguan.domain.entity.Image;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.ImageRepository;
import com.qkm.xinguan.repository.OssRepository;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author qiukangming
 * @version 1.0
 * @description oss 对象存储接口实现类
 * @since 2020/10/13 16:39
 */

@RestController
public class OssFileController implements IOssFileController {

    private final OssRepository ossRepository;

    private final ImageRepository imageRepository;

    @Autowired
    public OssFileController(OssRepository ossRepository, ImageRepository imageRepository) {
        this.ossRepository = ossRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "上传图片信息失败", operation = "OSS文件管理[上传图片]")
    public Result headUpLoad(MultipartFile file) {
        String s;
        try {
            s = ossRepository.upLoad(file);
        } catch (Exception e) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAIL, e);
        }
        return Result.ok().message("上传图片成功").data(s);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "删除信息失败", operation = "OSS文件管理[删除文件]")
    public Result deleteFile(String fileName) {
        try {
            // 删除阿里云OSS图片 和 数据库图片路径
            ossRepository.deleteFile(fileName);
        } catch (Exception e) {
            throw new BusinessException(ResultCode.FILE_DELETE_FAIL, e);
        }
        return Result.ok().message("文件删除成功");
    }

    @Override
    public Result listFile(Integer page, Integer size, @RequestBody ImageSearchForm imageSearchForm) {
        Page<Image> imagePage = imageRepository.listPage(page, size, imageSearchForm);
        return Result.ok().data(imagePage);
    }

}
