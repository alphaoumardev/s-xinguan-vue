package com.qkm.xinguan.domain.infrastructure.mapper;

import com.qkm.xinguan.domain.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色表 Mapper 接口
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据角色id启用或禁用角色
     *
     * @param id     角色id
     * @param status 状态
     */
    int updateRoleStatusById(Long id, Integer status);

    /**
     * 根据用户ID查询出角色名称集合
     *
     * @param id 用户ID
     * @return List<String>
     */
    List<String> queryRoleNamesByUserId(Long id);
}
