package com.qkm.xinguan.vo;

import lombok.Data;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/04 08:48
 * @description
 */

@Data
public class ProductStockVo {
    private Long id;

    private String name;

    private String pNum;

    private String model;

    private String unit;

    private String remark;

    private Long stock;

    private String imageUrl;
}
