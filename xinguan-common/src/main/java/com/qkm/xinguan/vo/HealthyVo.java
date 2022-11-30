package com.qkm.xinguan.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/06 22:09
 * @description 打卡记录视图对象
 */

@Data
public class HealthyVo {
    private Long id;

    private String address;

    private String username;

    private Integer situation;

    private Integer touch;

    private Integer passby;

    private Integer reception;

    private LocalDateTime createTime;
}
