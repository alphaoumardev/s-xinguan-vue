package com.qkm.xinguan.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qiukangming
 * @description 出库单数据表实体类
 * @since 2020-09-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("biz_out_stock")
@ApiModel(value = "OutStock对象", description = "OutStock对象")
public class OutStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "出库单")
    private String outNum;

    @ApiModelProperty(value = "出库类型:0:直接出库,1:审核出库")
    private Integer type;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "出库时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "出库总数")
    private Integer productNumber;

    @ApiModelProperty(value = "消费者id")
    private Long consumerId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "状态:0:正常入库,1:已进入回收,2:等待审核")
    private Integer status;

    @ApiModelProperty(value = "紧急程度:1:不急,2:常规,3:紧急4:特急")
    private Integer priority;

}
