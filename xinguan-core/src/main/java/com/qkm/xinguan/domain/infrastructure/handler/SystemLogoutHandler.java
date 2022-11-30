package com.qkm.xinguan.domain.infrastructure.handler;

import com.qkm.xinguan.domain.infrastructure.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/09 22:34
 * @description
 */
@Deprecated
public class SystemLogoutHandler implements LogoutHandler {

    @Value("${token.head}")
    private String head;

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        String token = RequestUtil.getHeadToken(httpServletRequest, head);
        if (!StringUtils.isEmpty(token)) {
            SecurityContextHolder.clearContext();
        }
    }
}
