package com.qkm.xinguan.domain.infrastructure.mapper;

import com.qkm.xinguan.domain.entity.Menu;
import com.qkm.xinguan.domain.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户表 Mapper 接口
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据用户id启用或禁用账户
     *
     * @param id     用户id
     * @param status 状态
     */
    int updateUserStatusById(Long id, Integer status);

    /**
     * 通过用户名获取用户信息
     *
     * @param username 用户名
     * @return User
     */
    User getByUsername(String username);

    /**
     * 根据用户 id 重置账户密码
     *
     * @param pwd 账户重置的密码
     * @param id  账户id
     * @return 影响行数
     */
    int resetPassword(String pwd, Long id);

    /**
     * 根据用户名获取用户头像
     *
     * @param username 用户名
     * @return String
     */
    String getUserAvatarByUsername(String username);

    /**
     * 通过用户名获取权限列表
     *
     * @param username 用户名
     * @param isAdmin 是否管理员
     * @return List<String>
     */
    List<String> getAuthListByUsername(String username, boolean isAdmin);

    /**
     * 根据ID获取用户名
     *
     * @param id id
     * @return String
     */
    String getUsername(Long id);

    /**
     * 根据用户名获取ID
     *
     * @param username 用户名
     * @return Long
     */
    Long getIdByUsername(String username);

    /**
     * 根据用户名获取菜单
     *
     * @param username 用户名
     * @param type 菜单类型
     * @return List<Menu>
     */
    List<Menu> getMenusByUsername(Integer type, String username);
}
