package com.qkm.xinguan.repository.impl;

import com.qkm.xinguan.domain.entity.UserRole;
import com.qkm.xinguan.domain.infrastructure.mapper.UserRoleMapper;
import com.qkm.xinguan.repository.UserRoleRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户角色关联表 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Service
public class UserRoleRepositoryImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleRepository {

    /**
     * 获取用户所拥有的角色IDS
     *
     * @param userId 用户id
     * @return List<Long>
     */
    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        return this.baseMapper.getRoleIdsByUserId(userId);
    }
}
