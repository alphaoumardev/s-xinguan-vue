package com.qkm.xinguan.domain.infrastructure.mapper;

import com.qkm.xinguan.domain.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户角色关联表 Mapper 接口
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 获取用户所拥有的角色IDS
     *
     * @param userId 用户id
     * @return List<Long>
     */
    List<Long> getRoleIdsByUserId(Long userId);
}
