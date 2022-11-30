package com.qkm.xinguan.domain.infrastructure.handler;

import com.qkm.xinguan.domain.infrastructure.utils.JsonWriterUtil;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/09 13:40
 * @description 未登录处理方案
 */

@Component
public class SystemAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        JsonWriterUtil.buildJsonResult(httpServletResponse, Result.error(ResultCode.USER_NOT_LOGIN));
    }
}
