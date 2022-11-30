package com.qkm.xinguan.repository;

import com.qkm.xinguan.domain.entity.RoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 角色菜单关联表 服务类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
public interface RoleMenuRepository extends IService<RoleMenu> {

    /**
     * 获取菜单ids列表，通过角色id
     *
     * @param id 角色id
     * @return List<Long>
     */
    List<Long> getMenuListByRoleId(Long id);
}
