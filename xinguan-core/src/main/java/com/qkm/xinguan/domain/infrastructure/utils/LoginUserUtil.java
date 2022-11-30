package com.qkm.xinguan.domain.infrastructure.utils;

import com.qkm.xinguan.domain.infrastructure.security.LoginUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/10 17:14
 * @description 登录用户工具类
 */

@SuppressWarnings("unused")
public class LoginUserUtil {

    /**
     * 增加 当前登录用户的过期时间
     *
     * @param sec 秒数
     */
    public static void addLoginUserExpireTime(long sec) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!Objects.isNull(authentication)) {
            if (authentication instanceof UsernamePasswordAuthenticationToken) {
                LoginUser loginUser = (LoginUser) authentication.getPrincipal();
                loginUser.setExpireTime(sec + loginUser.getExpireTime());
                UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }
        }
    }

    /**
     * 更新当前登录用户信息
     *
     * @param request request
     * @param loginUser 登录用户信息
     */
    public static void setSecurityLoginUser(HttpServletRequest request, LoginUser loginUser){
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
