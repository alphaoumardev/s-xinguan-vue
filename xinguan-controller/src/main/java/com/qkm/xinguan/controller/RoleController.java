package com.qkm.xinguan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.constant.SystemConst;
import com.qkm.xinguan.domain.infrastructure.annotation.ControllerEndpoint;
import com.qkm.xinguan.domain.transform.dto.MenuIdsBodyDTO;
import com.qkm.xinguan.domain.transform.dto.RoleDTO;
import com.qkm.xinguan.domain.transform.form.RoleSearchForm;
import com.qkm.xinguan.domain.entity.Role;
import com.qkm.xinguan.exception.BindingResultException;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.RoleMenuRepository;
import com.qkm.xinguan.repository.RoleRepository;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Objects;

/**
 * 角色表 前端控制器
 *
 * @author qiukangming
 * @since 2020-09-24
 */

@RestController
public class RoleController implements IRoleController {

    private final RoleRepository roleRepository;

    private final RoleMenuRepository roleMenuRepository;

    @Autowired
    public RoleController(RoleRepository roleRepository, RoleMenuRepository roleMenuRepository) {
        this.roleRepository = roleRepository;
        this.roleMenuRepository = roleMenuRepository;
    }

    @Override
    public Result getList(RoleSearchForm roleSearchForm, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        Page<Role> pageInfo = roleRepository.getList(roleSearchForm);
        return Result.ok().data(pageInfo);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "添加角色信息失败", operation = "角色管理[添加]")
    public Result add(RoleDTO roleDTO, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        if (roleDTO.getRoleCode().equals("ADMIN")) {
            throw new BusinessException("不能添加管理员角色！");
        }
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        try{
            boolean save = roleRepository.save(role);
            if (save) {
                return Result.ok().message("添加角色成功");
            } else {
                throw new BusinessException(ResultCode.ADD_ROLE);
            }
        } catch (Exception e){
            throw new BusinessException("添加角色失败或标识码已存在！");
        }
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "删除角色信息失败", operation = "角色管理[删除]")
    public Result delete(Long id) {
        Role role = roleRepository.getById(id);
        if (!Objects.isNull(role)) {
            if (role.getRoleCode().equals("ADMIN")) {
                throw new BusinessException("不能删除系统管理员角色！");
            }
        }
        boolean b = roleRepository.removeById(id);
        if (b) {
            return Result.ok().message("删除角色成功");
        } else {
            throw new BusinessException(ResultCode.DELETE_ROLE);
        }
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "更新角色信息失败", operation = "角色管理[更新]")
    public Result update(RoleDTO roleDTO, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        Role dbRole = roleRepository.getById(roleDTO.getId());
        if (!Objects.isNull(dbRole)) {
            if (!dbRole.getRoleCode().equals(roleDTO.getRoleCode())) {
                throw new BusinessException("不能修改角色标识码！");
            }
        }
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        boolean b = roleRepository.updateById(role);
        if (b) {
            return Result.ok().message("修改角色信息成功");
        } else {
            throw new BusinessException(ResultCode.EDIT_ROLE);
        }

    }

    @Override@ControllerEndpoint(exceptionMessage = "导出角色列表信息失败", operation = "角色管理[导出]")
    public void exportUserList(HttpServletResponse response, RoleSearchForm roleSearchForm, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        roleRepository.exportUserList(response, roleSearchForm);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "更新角色状态信息失败", operation = "角色管理[启/禁用角色]")
    public Result updateRoleStatus(Long id, Boolean forbidden) {
        int res = roleRepository.updateRoleStatusById(id, forbidden ? SystemConst.ACTIVE : SystemConst.FORBIDDEN);
        String msg = forbidden ? "启用" : "禁用";
        if (res == 1) {
            return Result.ok().message(msg + "角色成功");
        } else {
            throw new BusinessException(ResultCode.FORBIDDEN_ACCOUNT.getCode(), msg + "角色失败");
        }
    }

    @Override
    public Result getAllList() {
        List<Role> roles = roleRepository.list();
        return Result.ok().data(roles);
    }

    @Override
    public Result getMenuListByRoleId(Long id) {
        List<Long> menuIds = roleMenuRepository.getMenuListByRoleId(id);
        return Result.ok().data(menuIds);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "角色赋权失败", operation = "角色管理[分配权限]")
    public Result assignMenus(Long id, MenuIdsBodyDTO menuIds) {
        return roleRepository.assignMenus(id, menuIds);
    }
}

