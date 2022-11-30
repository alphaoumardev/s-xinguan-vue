package com.qkm.xinguan.domain.infrastructure.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/10 14:40
 * @description
 */
public class RequestUtil {
    public static String getHeadToken(HttpServletRequest request, String flag){
        return request.getHeader(flag);
    }
}
