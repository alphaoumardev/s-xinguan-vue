package com.qkm.xinguan.domain.infrastructure.filter;

import com.qkm.xinguan.domain.infrastructure.security.LoginUser;
import com.qkm.xinguan.domain.infrastructure.utils.RequestUtil;
import com.qkm.xinguan.domain.infrastructure.utils.UserUtil;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.response.ResultCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/10 17:46
 * @description 登录会话过滤器
 */

//@Deprecated
public class LoginSessionFilter implements Filter {

    @Value("${token.head}")
    private String head;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // 只校验有 token 的请求
        String token = RequestUtil.getHeadToken((HttpServletRequest) servletRequest, head);
        if (!StringUtils.isEmpty(token)) {
            LoginUser loginUser = UserUtil.getCurrentLoginUser();
            if (!Objects.isNull(loginUser)){
                long now = System.currentTimeMillis();
                if (now > loginUser.getExpireTime()) {
                    throw new BusinessException(ResultCode.USER_SESSION_INVALID);
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
