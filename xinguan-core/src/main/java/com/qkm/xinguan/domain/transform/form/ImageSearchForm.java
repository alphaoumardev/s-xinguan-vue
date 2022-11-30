package com.qkm.xinguan.domain.transform.form;

import lombok.Data;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/01/27 14:47
 * @description
 */

@Data
public class ImageSearchForm {
    /**
     * 媒体类型
     */
    private String mediaType;

    /**
     * 后缀名称
     */
    private String suffix;

    /**
     * 路径
     */
    private String path;
}
