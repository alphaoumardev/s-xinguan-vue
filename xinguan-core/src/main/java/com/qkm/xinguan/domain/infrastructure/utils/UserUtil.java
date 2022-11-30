package com.qkm.xinguan.domain.infrastructure.utils;

import com.qkm.xinguan.constant.SystemConst;
import com.qkm.xinguan.domain.infrastructure.security.LoginUser;
import com.qkm.xinguan.domain.transform.dto.UserDTO;
import com.qkm.xinguan.domain.entity.User;
import com.qkm.xinguan.vo.UserExportVo;
import com.qkm.xinguan.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author qiukangming
 * @version 1.0
 * @description 用户相关工具类
 * @since 2020/09/28 09:29
 */

public class UserUtil {

    /**
     * 构建 UserVO 对象
     *
     * @param user 用户 Entity
     * @return UserVO
     */
    public static UserVo executeVo(User user, String deptName) {
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        userVo.setForbidden(user.getForbidden() == 0);
        userVo.setSex(user.getSex().equals(SystemConst.MALE) ? "帅哥" : (user.getSex().equals(SystemConst.FEMALE) ? "美女" : "保密"));
        userVo.setDeptName(deptName);
        return userVo;
    }

    /**
     * 构建实体类对象
     *
     * @param userDTO 用户传输对象
     * @return User
     */
    public static User executeEntity(UserDTO userDTO, Long id) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setId(id);
        // 获取密码Bean
        PasswordEncoder passwordEncoder = SpringComponentUtil.getBean("passwordEncoder", PasswordEncoder.class);
        if (Objects.isNull(id) && StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(SystemConst.DEFAULT_PASSWORD));
        }
        user.setForbidden(SystemConst.ACTIVE);
        // 盐值
        user.setSalt(UUID.randomUUID().toString().substring(0, 32));
        user.setType(SystemConst.SYSTEM_ACCOUNT);
        return user;
    }

    /**
     * 比较密码
     *
     * @param pwd       前端输入
     * @param encodePwd 数据库中的密码
     * @return boolean
     */
    public static boolean matches(String pwd, String encodePwd) {
        return SpringComponentUtil.getBean("passwordEncoder", PasswordEncoder.class)
                .matches(pwd, encodePwd);
    }

    /**
     * 获取加密之后的密码
     *
     * @param pwd pwd
     * @return String
     */
    public static String encode(String pwd) {
        return SpringComponentUtil.getBean("passwordEncoder", PasswordEncoder.class).encode(pwd);
    }

    /**
     * 将 UserVo 列表转换成 UserExportVo 列表
     *
     * @param userVos UserVo 列表
     * @return List<UserExportVo>
     */
    public static List<UserExportVo> toExportVos(List<UserVo> userVos) {
        List<UserExportVo> userExportVos = new ArrayList<>(userVos.size());
        userVos.forEach(uv -> userExportVos.add(toExportVo(uv)));
        return userExportVos;
    }

    /**
     * 将 UserVo 转换成 UserExportVo
     *
     * @param userVo UserVo
     * @return UserExportVo
     */
    public static UserExportVo toExportVo(UserVo userVo) {
        UserExportVo uev = new UserExportVo();
        BeanUtils.copyProperties(userVo, uev);
        uev.setForbidden(userVo.getForbidden() ? "系统黑名单" : "正常用户");
        return uev;
    }

    /**
     * 获取当前登录用户信息
     *
     * @return LoginUser
     */
    public static LoginUser getCurrentLoginUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!Objects.isNull(authentication)) {
            if (authentication instanceof AnonymousAuthenticationToken) {
                return null;
            }
            if (authentication instanceof UsernamePasswordAuthenticationToken) {
                return (LoginUser) authentication.getPrincipal();
            }
        }
        return null;
    }
}
