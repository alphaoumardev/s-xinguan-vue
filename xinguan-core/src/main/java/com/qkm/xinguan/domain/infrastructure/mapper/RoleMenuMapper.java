package com.qkm.xinguan.domain.infrastructure.mapper;

import com.qkm.xinguan.domain.entity.RoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色菜单关联表 Mapper 接口
 *
 * @author qiukangming
 * @since 2020-09-24
 */

@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    /**
     * 根据角色id获取拥有的菜单ids
     *
     * @param id 角色id
     * @return List<Long>
     */
    List<Long> getMenuListByRoleId(Long id);
}
