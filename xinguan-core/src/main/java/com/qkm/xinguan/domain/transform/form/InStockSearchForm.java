package com.qkm.xinguan.domain.transform.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/03/29 21:50
 * @description
 */

@Data
public class InStockSearchForm {
    /**
     * 入库类型
     */
    private Integer type;

    /**
     * 单号
     */
    private String inNum;

    /**
     * 入库状态
     */
    private Integer status;

    /**
     * 开始日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
