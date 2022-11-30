package com.qkm.xinguan.domain.transform.form;

import lombok.Data;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/01 15:23
 * @description 系统操作日志查询条件实体
 */

@Data
public class LogSearchForm {

    /**
     * 操作人
     */
    private String username;

    /**
     * ip 地址
     */
    private String ip;

    /**
     * 操作未位置
     */
    private String location;
}
