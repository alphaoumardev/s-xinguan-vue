package com.qkm.xinguan.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.transform.form.ImageSearchForm;
import com.qkm.xinguan.domain.entity.Image;
import com.qkm.xinguan.domain.infrastructure.mapper.ImageMapper;
import com.qkm.xinguan.repository.ImageRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 图片 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Service
public class ImageRepositoryImpl extends ServiceImpl<ImageMapper, Image> implements ImageRepository {

    /**
     * 分页获取图片列表
     *
     * @param page            page
     * @param size            size
     * @param imageSearchForm form
     * @return Page<Image>
     */
    @Override
    public Page<Image> listPage(Integer page, Integer size, ImageSearchForm imageSearchForm) {
        LambdaQueryWrapper<Image> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(imageSearchForm.getMediaType())) {
            wrapper.eq(Image::getMediaType, imageSearchForm.getMediaType());
        }
        if (!StringUtils.isEmpty(imageSearchForm.getPath())) {
            wrapper.like(Image::getPath, imageSearchForm.getPath());
        }
        if (!StringUtils.isEmpty(imageSearchForm.getSuffix())) {
            wrapper.eq(Image::getSuffix, imageSearchForm.getSuffix());
        }
        wrapper.orderByDesc(Image::getCreateTime);
        return this.baseMapper.selectPage(new Page<>(page, size), wrapper);
    }
}
