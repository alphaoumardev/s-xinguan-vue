package com.qkm.xinguan.domain.infrastructure.handler;

import com.qkm.xinguan.domain.infrastructure.utils.JsonWriterUtil;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/09 22:28
 * @description
 */

@Primary
@Component
public class SystemAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException {
        JsonWriterUtil.buildJsonResult(httpServletResponse, Result.error(ResultCode.NOT_AUTH));
    }
}
