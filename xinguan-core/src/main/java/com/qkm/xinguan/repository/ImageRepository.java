package com.qkm.xinguan.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.transform.form.ImageSearchForm;
import com.qkm.xinguan.domain.entity.Image;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 图片 服务类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
public interface ImageRepository extends IService<Image> {

    /**
     * 分页获取图片列表
     *
     * @param page            page
     * @param size            size
     * @param imageSearchForm form
     * @return Page<Image>
     */
    Page<Image> listPage(Integer page, Integer size, ImageSearchForm imageSearchForm);
}
