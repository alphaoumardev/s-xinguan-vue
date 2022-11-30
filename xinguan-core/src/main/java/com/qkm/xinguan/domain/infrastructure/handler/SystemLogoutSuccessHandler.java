package com.qkm.xinguan.domain.infrastructure.handler;

import com.qkm.xinguan.domain.infrastructure.utils.JsonWriterUtil;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/09 22:35
 * @description
 */

@Deprecated
public class SystemLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        JsonWriterUtil.buildJsonResult(httpServletResponse, Result.ok(ResultCode.LOGOUT_SUCCESS));
    }
}
