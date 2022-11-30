package com.qkm.xinguan.response;

/**
 * @author qiukangming
 * @version 1.0
 * @description 结果状态信息接口
 * @since 2020/09/24 11:00
 */

public interface CustomizeResultCode {

    /**
     * 获取错误状态码
     *
     * @return 状态码
     */
    Integer getCode();

    /**
     * 获取错误信息
     *
     * @return String
     */
    String getMessage();
}
