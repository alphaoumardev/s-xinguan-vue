package com.qkm.xinguan.repository.impl;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qkm.xinguan.constant.SystemConst;
import com.qkm.xinguan.domain.entity.Menu;
import com.qkm.xinguan.domain.infrastructure.converter.MenuConverter;
import com.qkm.xinguan.domain.infrastructure.mapper.MenuMapper;
import com.qkm.xinguan.domain.infrastructure.security.LoginUser;
import com.qkm.xinguan.domain.infrastructure.utils.ExcelStyleUtil;
import com.qkm.xinguan.domain.infrastructure.utils.ExcelUtil;
import com.qkm.xinguan.domain.infrastructure.utils.MenuTreeBuilderUtil;
import com.qkm.xinguan.domain.infrastructure.utils.UserUtil;
import com.qkm.xinguan.domain.transform.dto.MenuDTO;
import com.qkm.xinguan.domain.transform.form.MenuSearchForm;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.MenuRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import com.qkm.xinguan.vo.MenuExportVo;
import com.qkm.xinguan.vo.MenuNodeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单表 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuRepository {

    @Resource
    private MenuMapper menuMapper;

    private static final List<Long> openIds = new ArrayList<>();

    /**
     * 获取菜单树, 菜单和权限按钮
     *
     * @return List<MenuNodeVo>
     */
    public List<MenuNodeVo> findMenuAndButton() {
        List<Menu> menus = this.baseMapper.selectList(null);
        // 设置展开项
        openIds.clear();
        menus.forEach(menu -> {
            if (menu.getOpen().equals(SystemConst.OPEN))
                openIds.add(menu.getId());
        });
        List<MenuNodeVo> menuNodeVos = MenuConverter.converterToMenus(menus);
        return MenuTreeBuilderUtil.build(menuNodeVos);
    }

    /**
     * 获取菜单树, 只获取菜单
     *
     * @return List<MenuNodeVo>
     */
    @Override
    public Result findMenusAndUrl() {
        //从当前登录用户中获取菜单
        LoginUser loginUser = UserUtil.getCurrentLoginUser();
        assert loginUser != null;
        List<Menu> menus = loginUser.getMenus();
        List<String> urls = menus.parallelStream().map(Menu::getUrl).filter(url -> !StringUtils.isEmpty(url)).collect(Collectors.toList());
        List<MenuNodeVo> menuNodeVos = MenuConverter.converterToMenus(menus);
        Map<String, Object> data = new HashMap<>(2);
        data.put("menus", MenuTreeBuilderUtil.build(menuNodeVos));
        // 添加系统公共菜单路径
        urls.add("/main");
        urls.add("/system/welcome");
        urls.add("/common/404");
        data.put("urls", urls);
        return Result.ok().data(data);
    }

    /**
     * 获取需要展开的菜单 ID
     *
     * @return List<Integer>
     */
    @Override
    public List<Long> getOpenIds() {
        return openIds;
    }

    /**
     * 更新菜单信息
     *
     * @param menuDTO 传输数据
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result update(MenuDTO menuDTO) {
        // 获取原来的数据
        Menu oldMenu = this.baseMapper.selectById(menuDTO.getId());
        if (Objects.isNull(oldMenu)) {
            return Result.error().message("当前操作的资源不存在，请刷新重试！");
        }
        // 如果是变成按钮，查找是否含有子项
        if (menuDTO.getType().equals(SystemConst.BUTTON)) {
            Integer count = menuMapper.checkHasChildren(menuDTO.getId());
            if (count > 0) {
                return Result.error().message("当前资源含有一个或多个子项, 无法将其设定为资源按钮！");
            }
        }
        // 如果原来的状态和现在的状态不一致，则须执行此代码块
        Integer nowStatus = menuDTO.getDisabled() ? SystemConst.FORBIDDEN : SystemConst.ACTIVE;
        if (!oldMenu.getAvailable().equals(nowStatus) && Objects.nonNull(menuDTO.getIds()) && !menuDTO.getIds().isEmpty()) {
            // 不为 null 且长度大于 0, 更新子菜单/按钮的状态
            menuMapper.updateChildrens(menuDTO.getIds(), nowStatus);
        }
        // 如果父级设置为禁用，需要将其子项设置为禁用，如果父级设置为启用，所有的子项也设置为启用
        Menu menu = new Menu();
        menu.setAvailable(nowStatus);
        BeanUtils.copyProperties(menuDTO, menu);
        int row = this.baseMapper.updateById(menu);
        if (row > 0) {
            return Result.ok();
        } else {
            throw new BusinessException(ResultCode.FAILED);
        }
    }

    /**
     * 添加菜单/按钮信息
     *
     * @param menuDTO 传输数据
     * @return Result
     */
    @Override
    public Result submitMenu(MenuDTO menuDTO) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(menuDTO, menu);
        menu.setAvailable(menuDTO.getDisabled() ? SystemConst.FORBIDDEN : SystemConst.ACTIVE);
        int row = this.baseMapper.insert(menu);
        if (row > 0) {
            return Result.ok();
        } else {
            throw new BusinessException(ResultCode.FAILED);
        }
    }

    /**
     * 删除菜单/按钮
     *
     * @param id 菜单/按钮 ID
     * @return Result
     */
    @Override
    public Result deleteMenu(Long id) {
        // 获取原来的数据
        Menu oldMenu = this.baseMapper.selectById(id);
        if (Objects.isNull(oldMenu)) {
            return Result.error().message("当前操作的资源不存在，请刷新重试！");
        }
        Integer count = menuMapper.checkHasChildren(id);
        if (count > 0) {
            return Result.error().message("当前资源含有一个或多个子项, 无法将其删除！");
        }
        // 可以删除
        int row = menuMapper.deleteById(id);
        if (row > 0) {
            return Result.ok();
        } else {
            throw new BusinessException(ResultCode.FAILED);
        }
    }

    /**
     * 导出菜单列表
     *
     * @param response       响应头
     * @param menuSearchForm 查询实体数据包
     */
    @Override
    public void exportMenuList(HttpServletResponse response, MenuSearchForm menuSearchForm) {
        LambdaQueryWrapper<Menu> wrapper = executeWrapper(menuSearchForm);
        List<Menu> menus = this.baseMapper.selectList(wrapper);
        // 将 Role 转换成 导出对象
        List<MenuExportVo> exportVos = toMenuExportVo(menus);
        // 导出
        try {
            ExportParams params = new ExportParams("菜单/资源信息表格", "sheet1", ExcelType.XSSF);
            params.setStyle(ExcelStyleUtil.class);
            ExcelUtil.exportExcel(exportVos, MenuExportVo.class, "角菜单/资源信息表", params, response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ResultCode.EXPORT_FAIL);
        }
    }

    private List<MenuExportVo> toMenuExportVo(List<Menu> menus) {
        List<MenuExportVo> exportVos = new ArrayList<>(menus.size());
        List<Long> menuIds = new ArrayList<>(menus.size());
        menus.forEach(menu -> {
            MenuExportVo menuExportVo = new MenuExportVo();
            BeanUtils.copyProperties(menu, menuExportVo);
            menuExportVo.setType(menu.getType().equals(SystemConst.MENU) ? "菜单" : "按钮");
            menuExportVo.setAvailable(menu.getAvailable().equals(SystemConst.FORBIDDEN) ? "禁用" : "启用");
            menuExportVo.setOpen(menu.getOpen().equals(SystemConst.OPEN) ? "默认展开" : "默认闭合");
            exportVos.add(menuExportVo);
            // 获取菜单的 id 集合
            menuIds.add(menu.getParentId());
        });
        // 根据菜单IDS获取菜单名称
        List<String> parentMenuNames = this.baseMapper.getMenuNameListByIds(menuIds);
        for (int i = 0; i < parentMenuNames.size(); i++) {
            String name = parentMenuNames.get(i);
            exportVos.get(i).setParentName(StringUtils.isEmpty(name) ? "没有父节点" : name);
        }
        return exportVos;
    }

    private LambdaQueryWrapper<Menu> executeWrapper(MenuSearchForm menuSearchForm) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(menuSearchForm.getNameFilter())) {
            wrapper.like(Menu::getMenuName, menuSearchForm.getNameFilter());
        }
        return wrapper;
    }
}
