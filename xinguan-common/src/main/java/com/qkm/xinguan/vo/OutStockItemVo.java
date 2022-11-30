package com.qkm.xinguan.vo;

import lombok.Data;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/05 19:25
 * @description
 */

@Data
public class OutStockItemVo {
    private Long id;

    private String pNum;

    private String name;

    private String model;

    private String unit;

    private String imageUrl;

    private int count;
}
