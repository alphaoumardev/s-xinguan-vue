package com.qkm.xinguan.domain.infrastructure.handler;

import com.qkm.xinguan.domain.entity.User;
import com.qkm.xinguan.domain.infrastructure.mapper.RoleMapper;
import com.qkm.xinguan.domain.infrastructure.utils.JsonWriterUtil;
import com.qkm.xinguan.domain.infrastructure.utils.JwtUtil;
import com.qkm.xinguan.domain.infrastructure.utils.UserUtil;
import com.qkm.xinguan.repository.DepartmentRepository;
import com.qkm.xinguan.repository.LoginLogRepository;
import com.qkm.xinguan.repository.UserRepository;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/09 13:11
 * @description 登录成功处理方案
 */

@Component
public class SystemAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    private final DepartmentRepository departmentRepository;

    private final JwtUtil jwtUtil;

    private final LoginLogRepository loginLogRepository;

    private final RoleMapper roleMapper;

    @Autowired
    public SystemAuthenticationSuccessHandler(UserRepository userRepository, DepartmentRepository departmentRepository, JwtUtil jwtUtil, LoginLogRepository loginLogRepository, RoleMapper roleMapper) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.jwtUtil = jwtUtil;
        this.loginLogRepository = loginLogRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        User user = userRepository.getByUserName(authentication.getName());
        String deptName = departmentRepository.getDeptNameById(user.getDepartmentId());
        Map<String, Object> loginData = new HashMap<>(2);
        // 查询用户所拥有的角色信息
        List<String> names = roleMapper.queryRoleNamesByUserId(user.getId());
        UserVo userVo = UserUtil.executeVo(user, deptName);
        userVo.setRoles(String.join(",", names));
        // 载入用户信息
        loginData.put("data", userVo);
        // 创建令牌
        String token = jwtUtil.generateToken(user.getUsername());
        loginData.put("token", token);
        // 记录登录日志
        loginLogRepository.saveLoginLog(authentication.getName(), httpServletRequest);
        JsonWriterUtil.buildJsonResult(httpServletResponse, Result.ok().data(loginData));
    }
}
