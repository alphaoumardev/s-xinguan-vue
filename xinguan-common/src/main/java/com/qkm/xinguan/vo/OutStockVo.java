package com.qkm.xinguan.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/05 17:05
 * @description
 */

@Data
public class OutStockVo {

    private Long id;

    private String outNum;

    private Integer type;

    private String operator;

    private LocalDateTime createTime;

    private Integer productNumber;

    private Integer priority;

    //发放的物资列表
    private List<Object> products = new ArrayList<>();

    private String remark;

    //发放单的状态
    private Integer status;

    /*** 如果consumerId不为空**/

    private Long consumerId;

    //去向名
    private String name;

    //地址
    private String address;

    //联系电话
    private String phone;

    //联系人
    private String contact;

    //排序
    private Integer sort;

}
