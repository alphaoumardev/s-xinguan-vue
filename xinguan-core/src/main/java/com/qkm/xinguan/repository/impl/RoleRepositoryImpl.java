package com.qkm.xinguan.repository.impl;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.constant.SystemConst;
import com.qkm.xinguan.domain.entity.*;
import com.qkm.xinguan.domain.transform.dto.MenuIdsBodyDTO;
import com.qkm.xinguan.domain.transform.form.RoleSearchForm;
import com.qkm.xinguan.domain.infrastructure.mapper.RoleMapper;
import com.qkm.xinguan.domain.infrastructure.utils.ExcelStyleUtil;
import com.qkm.xinguan.domain.infrastructure.utils.ExcelUtil;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.MenuRepository;
import com.qkm.xinguan.repository.RoleMenuRepository;
import com.qkm.xinguan.repository.RoleRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import com.qkm.xinguan.vo.RoleExportVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 角色表 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Service
public class RoleRepositoryImpl extends ServiceImpl<RoleMapper, Role> implements RoleRepository {

    private final RoleMenuRepository roleMenuRepository;

    private final MenuRepository menuRepository;

    @Autowired
    public RoleRepositoryImpl(RoleMenuRepository roleMenuRepository, MenuRepository menuRepository) {
        this.roleMenuRepository = roleMenuRepository;
        this.menuRepository = menuRepository;
    }

    /**
     * 分页查询角色信息
     *
     * @param roleSearchForm roleFrom
     * @return Page<Role>
     */
    @Override
    public Page<Role> getList(RoleSearchForm roleSearchForm) {
        LambdaQueryWrapper<Role> wrapper = executeWrapper(roleSearchForm);
        return this.baseMapper.selectPage(new Page<>(roleSearchForm.getPage(), roleSearchForm.getSize()), wrapper);
    }

    /**
     * 导出角色列表信息
     *
     * @param response       响应头
     * @param roleSearchForm 查询对象
     */
    @Override
    public void exportUserList(HttpServletResponse response, RoleSearchForm roleSearchForm) {
        LambdaQueryWrapper<Role> wrapper = executeWrapper(roleSearchForm);
        List<Role> roles = this.baseMapper.selectList(wrapper);
        // 将 Role 转换成 导出对象
        List<RoleExportVo> exportVos = toRoleExportVo(roles);
        // 导出
        try {
            ExportParams params = new ExportParams("角色信息表格", "sheet1", ExcelType.XSSF);
            params.setStyle(ExcelStyleUtil.class);
            ExcelUtil.exportExcel(exportVos, RoleExportVo.class, "角色信息表", params, response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ResultCode.EXPORT_FAIL);
        }
    }

    /**
     * 修改角色的禁用状态
     *
     * @param id     角色ID
     * @param status 状态
     * @return int 影响行数
     */
    @Override
    public int updateRoleStatusById(Long id, Integer status) {
        Role role = this.baseMapper.selectById(id);
        if (!Objects.isNull(role)) {
            if (role.getRoleCode().equals("ADMIN")) {
                throw new BusinessException("不能操作系统管理员！");
            }
        }
        return this.baseMapper.updateRoleStatusById(id, status);
    }

    /**
     * 角色授权菜单
     *
     * @param id      角色id
     * @param menuIds 菜单IDS数据包
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result assignMenus(Long id, MenuIdsBodyDTO menuIds) {
        Long[] mids = menuIds.getMids();
        Role role = this.baseMapper.selectById(id);
        if (Objects.isNull(role)) {
            throw new BusinessException(ResultCode.ROLE_NOT_EXIST);
        }
        List<Long> menuIdsList = roleMenuRepository.getMenuListByRoleId(id);
        // 删除角色之前所拥有的菜单
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, id);
        boolean res = roleMenuRepository.remove(wrapper);

        // 如果需要分配菜单，进入此循环
        if (!Objects.isNull(mids) && mids.length > 0) {
            List<RoleMenu> roleMenus = new ArrayList<>(mids.length);
            for (Long mid : mids) {
                Menu menu = menuRepository.getById(mid);
                // 如果菜单不存在
                if (Objects.isNull(menu)) {
                    throw new BusinessException(ResultCode.MENU_NOT_EXIST);
                }
                // 如果菜单被禁用
                if (menu.getAvailable().equals(SystemConst.FORBIDDEN)) {
                    throw new BusinessException(ResultCode.MENU_FORBIDDEN);
                }
                RoleMenu rm = new RoleMenu();
                rm.setRoleId(id);
                rm.setMenuId(mid);
                rm.setMenuType(Long.parseLong(menu.getType()));
                roleMenus.add(rm);
            }
            // 批量插入表中
            boolean result = roleMenuRepository.saveBatch(roleMenus);
            if (result) {
                return Result.ok().message("成功为该角色分配菜单权限！");
            } else {
                throw new BusinessException(ResultCode.ASSIGN_MENUS_ERROR);
            }
        }
        if (res || menuIdsList.isEmpty()) {
            return Result.ok().message("清除角色菜单列表成功！");
        } else {
            throw new BusinessException(ResultCode.REMOVE_MENUS_ERROR);
        }
    }

    private static LambdaQueryWrapper<Role> executeWrapper(RoleSearchForm searchForm) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(searchForm.getRoleName())) {
            wrapper.like(Role::getRoleName, searchForm.getRoleName());
        }
        return wrapper;
    }

    private static List<RoleExportVo> toRoleExportVo(List<Role> roles) {
        List<RoleExportVo> exportVos = new ArrayList<>(roles.size());
        roles.forEach(role -> {
            RoleExportVo exportVo = new RoleExportVo();
            BeanUtils.copyProperties(role, exportVo);
            exportVo.setForbidden(role.getForbidden().equals(SystemConst.FORBIDDEN) ? "禁用" : "启用");
            exportVos.add(exportVo);
        });
        return exportVos;
    }
}
