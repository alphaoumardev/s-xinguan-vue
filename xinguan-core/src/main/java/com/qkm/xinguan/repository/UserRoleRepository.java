package com.qkm.xinguan.repository;

import com.qkm.xinguan.domain.entity.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户角色关联表 服务类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
public interface UserRoleRepository extends IService<UserRole> {

    /**
     * 获取用户所拥有的角色IDS
     *
     * @param userId 用户id
     * @return List<Long>
     */
    List<Long> getRoleIdsByUserId(Long userId);
}
