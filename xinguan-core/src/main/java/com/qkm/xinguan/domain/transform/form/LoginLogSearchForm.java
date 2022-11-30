package com.qkm.xinguan.domain.transform.form;

import lombok.Data;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/03/15 15:14
 * @description
 */

@Data
public class LoginLogSearchForm {

    /**
     * page
     */
    private Integer page;

    /**
     * size
     */
    private Integer size;

    /**
     * 用户名
     */
    private String username;

    /**
     * IP
     */
    private String ip;

    /**
     * 登录地址
     */
    private String location;
}
