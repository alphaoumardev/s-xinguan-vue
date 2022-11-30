package com.qkm.xinguan.domain.infrastructure.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/03/08 21:05
 * @description 客户端输出工具
 */
@SuppressWarnings("unused")
public class JsonWriterUtil {

    /**
     * 向请求发送 Json 数据
     *
     * @param response response 响应头
     * @param data 需要返回的数据，通常是前后端分离的 Result
     * @throws IOException IO 异常
     */
    public static void buildJsonResult(HttpServletResponse response, Object data) throws IOException {
        /*
         * 设置允许的方法和请求
         */
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Method", "POST,GET,PUT,DELETE");
        /*
         * 输出JSON
         */
        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(data));
        out.flush();
        out.close();
    }

}
