package com.qkm.xinguan.domain.infrastructure.security;

import com.qkm.xinguan.constant.SystemConst;
import com.qkm.xinguan.domain.entity.Menu;
import com.qkm.xinguan.domain.entity.Role;
import com.qkm.xinguan.domain.entity.User;
import com.qkm.xinguan.domain.infrastructure.mapper.RoleMapper;
import com.qkm.xinguan.domain.infrastructure.mapper.UserMapper;
import com.qkm.xinguan.domain.infrastructure.mapper.UserRoleMapper;
import com.qkm.xinguan.domain.infrastructure.utils.JsonWriterUtil;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/03/04 22:37
 * @description 用户登录实现类，主要由于框架是 SpringSecurity
 */
@AllArgsConstructor
@Service("systemUserDetailsService")
public class SystemUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    private final RoleMapper roleMapper;

    private final UserRoleMapper userRoleMapper;

//    @Autowired
//    public SystemUserDetailsService(UserMapper userMapper, RoleMapper roleMapper, UserRoleMapper userRoleMapper) {
//        this.userMapper = userMapper;
//        this.roleMapper = roleMapper;
//        this.userRoleMapper = userRoleMapper;
//    }

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String s) {
        User user = userMapper.getByUsername(s);
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
        assert response != null;
        if (Objects.isNull(user)) {
            JsonWriterUtil.buildJsonResult(response, Result.error(ResultCode.USER_ACCOUNT_NOT_EXIST));
            return null;
        }

        // 查询出角色信息，角色是否被禁用
        List<Long> roleIds = userRoleMapper.getRoleIdsByUserId(user.getId());
        // 如果没有分配角色
        if (roleIds.isEmpty()) {
            JsonWriterUtil.buildJsonResult(response, Result.error(ResultCode.DONT_HAVE_ANY_ROLE));
            return null;
        }
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        List<String> codes = roles.stream().map(Role::getRoleCode).collect(Collectors.toList());
        if (!codes.contains("ADMIN")) {
            // 如果没有超级管理员的角色，只有所拥有的所有角色都被禁用了，才算完全角色被禁用，不能登录
            List<Integer> flags = roles.stream().map(Role::getForbidden).collect(Collectors.toList());
            if (!flags.contains(SystemConst.ACTIVE)) {
                JsonWriterUtil.buildJsonResult(response, Result.error(ResultCode.ROLE_ALL_IS_FORBIDDEN));
                return null;
            }
        }

        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(user, loginUser);

        // 如果用户存在则要查询用户的角色集合
        List<String> codeList = userMapper.getAuthListByUsername(user.getUsername(), SystemConst.ADMIN.equals(user.getType()));
        // 获取用户所拥有的菜单, 根据用户名
        List<Menu> menus = userMapper.getMenusByUsername(Integer.parseInt(SystemConst.MENU), user.getUsername());
        loginUser.setAuthorities(codeList);
        loginUser.setMenus(menus);

        return loginUser;
    }
}
