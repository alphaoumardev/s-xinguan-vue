package com.qkm.xinguan.domain.infrastructure.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qkm.xinguan.domain.infrastructure.config.CustomCacheConfig;
import com.qkm.xinguan.domain.transform.dto.LoginDTO;
import com.qkm.xinguan.repository.CustomCacheRepository;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/08 22:43
 * @description
 */

public class SecurityLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final CustomCacheRepository customCacheRepository;

    public SecurityLoginFilter(CustomCacheRepository customCacheRepository) {
        this.customCacheRepository = customCacheRepository;
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/system/user/login", "POST"));
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = request.getInputStream();
        LoginDTO loginBody = objectMapper.readValue(inputStream, LoginDTO.class);
        // 判断用户名和密码是否携带
        if (StringUtils.isEmpty(loginBody.getUsername()) || StringUtils.isEmpty(loginBody.getPassword())) {
            throw new InternalAuthenticationServiceException("用户名或密码不能为空！");
        }
        // 判断验证码是否携带
        if (StringUtils.isEmpty(loginBody.getVerifyCode())) {
            throw new AuthenticationServiceException("请输入验证码！");
        }
        // 验证验证码
        Long interval = customCacheRepository.getKeyInterval(CustomCacheConfig.LOGIN_VERIFY_CODE + loginBody.getVerifyCodeId());
        if (interval == null || interval <= 3) {
            throw new AuthenticationServiceException("验证码已过期, 请重新点击图片生成新的验证码！");
        }
        String code = customCacheRepository.getLoginVerifyCode(loginBody.getVerifyCodeId());
        if (!loginBody.getVerifyCode().equalsIgnoreCase(code)) {
            throw new AuthenticationServiceException("验证码错误, 请重试！");
        }
        customCacheRepository.deleteLoginVerifyCode(loginBody.getVerifyCodeId());
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginBody.getUsername(), loginBody.getPassword());
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

}
