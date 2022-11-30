package com.qkm.xinguan.repository.impl;

import com.qkm.xinguan.domain.entity.Role;
import com.qkm.xinguan.domain.entity.RoleMenu;
import com.qkm.xinguan.domain.infrastructure.mapper.RoleMapper;
import com.qkm.xinguan.domain.infrastructure.mapper.RoleMenuMapper;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.RoleMenuRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qkm.xinguan.response.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 角色菜单关联表 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Service
public class RoleMenuRepositoryImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuRepository {

    private final RoleMapper roleMapper;

    @Autowired
    public RoleMenuRepositoryImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    /**
     * 获取菜单ids列表，通过角色id
     *
     * @param id 角色id
     * @return List<Long>
     */
    @Override
    public List<Long> getMenuListByRoleId(Long id) {
        Role role = roleMapper.selectById(id);
        if (Objects.isNull(role)) {
            throw new BusinessException(ResultCode.ROLE_NOT_EXIST);
        }
        return this.baseMapper.getMenuListByRoleId(id);
    }
}
