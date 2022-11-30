package com.qkm.xinguan.domain.transform.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/06 22:03
 * @description 打卡记录查询对象
 */

@Data
public class PointSearchForm {

    private String province;

    private String city;

    private String origin;

    private String username;

    private Integer situation;

    private Integer touch;

    private Integer passby;

    private Integer reception;

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
