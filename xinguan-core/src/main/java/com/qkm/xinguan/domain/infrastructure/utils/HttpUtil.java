package com.qkm.xinguan.domain.infrastructure.utils;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.Objects;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/01/23 11:04
 * @description Http 请求工具类，发送请求
 */

@SuppressWarnings("unused")
public class HttpUtil {
    /**
     * 设置头信息
     */
    public static final MediaType HEAD = MediaType.parse("application/json;charset=utf-8");

    /**
     * 发送 POST 请求获取信息
     *
     * @param url  接口地址
     * @param body 请求体参数json字符串
     * @return String
     */
    public static String httpPost(String url, String body) throws Exception {
        String postData;
        OkHttpClient httpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(HEAD, body);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = httpClient.newCall(request).execute();
        postData = Objects.requireNonNull(response.body()).string();
        return postData;
    }

    /**
     * 发送 GET 请求获取数据
     *
     * @param url 接口地址
     * @return String
     */
    public static String httpGet(String url) throws Exception {

        String getData;
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = httpClient.newCall(request).execute();
        getData = Objects.requireNonNull(response.body()).string();
        return getData;
    }
}
