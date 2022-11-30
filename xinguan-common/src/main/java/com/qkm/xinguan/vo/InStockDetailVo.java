package com.qkm.xinguan.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/01 20:19
 * @description
 */

@Data
public class InStockDetailVo {
    private String inNum;

    private Integer status;

    private Integer type;

    private String operator;

    private SupplierVo supplierVo;

    /** 总数**/
    private long total;

    private List<InStockItemVo> itemVos = new ArrayList<>();
}
