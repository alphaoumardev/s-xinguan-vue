package com.qkm.xinguan.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/05 19:25
 * @description
 */

@Data
public class OutStockDetailVo {

    private String outNum;

    private Integer status;

    private Integer type;

    private String operator;

    private ConsumerVo consumerVo;

    /**
     * 总数
     **/
    private long total;

    private List<OutStockItemVo> itemVos = new ArrayList<>();
}
