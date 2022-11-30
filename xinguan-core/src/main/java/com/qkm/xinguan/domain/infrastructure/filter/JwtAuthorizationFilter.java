package com.qkm.xinguan.domain.infrastructure.filter;

import com.qkm.xinguan.domain.infrastructure.security.LoginUser;
import com.qkm.xinguan.domain.infrastructure.utils.*;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/10 14:53
 * @description Jwt 过滤器
 */

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Value("${token.head}")
    private String head;

    private final JwtUtil jwtUtil;

    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthorizationFilter(JwtUtil jwtUtil, @Qualifier("systemUserDetailsService") UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String token = RequestUtil.getHeadToken(httpServletRequest, head);
        // 如果不为空，设置 SecurityContext 的值
        if (!StringUtils.isEmpty(token)) {
            if(!jwtUtil.isJwtStr(token)){ //不是 jwt 形式的字符串 或者
                JsonWriterUtil.buildJsonResult(httpServletResponse, Result.error(ResultCode.JWT_FORMAT_ERROR));
                return;
            }
            // 验证有效性
            if (jwtUtil.isExpired(token) || Objects.isNull(jwtUtil.getClaimByToken(token))) {
                JsonWriterUtil.buildJsonResult(httpServletResponse, Result.error(ResultCode.JWT_IS_EXPIRED));
                return;
            }
            // 以上验证全部通过，则设置 SecurityContext 的信息
            LoginUser loginUser = UserUtil.getCurrentLoginUser();
            if (Objects.isNull(loginUser)){
                loginUser = (LoginUser) userDetailsService.loadUserByUsername(jwtUtil.getAccountName(token));
                LoginUserUtil.setSecurityLoginUser(httpServletRequest, loginUser);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
