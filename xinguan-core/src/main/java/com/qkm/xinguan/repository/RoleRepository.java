package com.qkm.xinguan.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.transform.dto.MenuIdsBodyDTO;
import com.qkm.xinguan.domain.transform.form.RoleSearchForm;
import com.qkm.xinguan.domain.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qkm.xinguan.response.Result;

import javax.servlet.http.HttpServletResponse;

/**
 * 角色表 服务类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
public interface RoleRepository extends IService<Role> {

    /**
     * 分页查询角色信息
     *
     * @param roleSearchForm roleFrom
     * @return Page<Role>
     */
    Page<Role> getList(RoleSearchForm roleSearchForm);

    /**
     * 导出角色列表信息
     *
     * @param response       响应头
     * @param roleSearchForm 查询对象
     */
    void exportUserList(HttpServletResponse response, RoleSearchForm roleSearchForm);

    /**
     * 修改角色的禁用状态
     *
     * @param id     角色ID
     * @param status 状态
     * @return int 影响行数
     */
    int updateRoleStatusById(Long id, Integer status);

    /**
     * 角色授权菜单
     *
     * @param id 角色id
     * @param menuIds 菜单IDS数据包
     * @return Result
     */
    Result assignMenus(Long id, MenuIdsBodyDTO menuIds);
}
