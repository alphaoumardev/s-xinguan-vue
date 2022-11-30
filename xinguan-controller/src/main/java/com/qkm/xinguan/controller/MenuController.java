package com.qkm.xinguan.controller;

import com.qkm.xinguan.domain.infrastructure.annotation.ControllerEndpoint;
import com.qkm.xinguan.domain.transform.dto.MenuDTO;
import com.qkm.xinguan.domain.transform.form.MenuSearchForm;
import com.qkm.xinguan.exception.BindingResultException;
import com.qkm.xinguan.repository.MenuRepository;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.MenuNodeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单表 前端控制器
 *
 * @author qiukangming
 * @since 2020-09-24
 */

@RestController
public class MenuController implements IMenuController {

    private final MenuRepository menuRepository;

    @Autowired
    public MenuController(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    /**
     * 获取菜单树
     *
     * @return Result
     */
    @Override
    public Result getMenuAndButton() {
        List<MenuNodeVo> menuTree = menuRepository.findMenuAndButton();
        List<Long> openIds = menuRepository.getOpenIds();
        Map<String, Object> res = new HashMap<>(2);
        res.put("menus", menuTree);
        res.put("ids", openIds);
        return Result.ok().data(res);
    }

    /**
     * 获取菜单
     *
     * @return Result
     */
    @Override
    public Result getMenus() {
        return menuRepository.findMenusAndUrl();
    }

    /**
     * 编辑，修改菜单
     *
     * @return Result
     */
    @Override
    @ControllerEndpoint(exceptionMessage = "修改菜单/权限信息失败", operation = "菜单/权限管理[修改]")
    public Result modifyMenu(@RequestBody MenuDTO menuDTO, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        return menuRepository.update(menuDTO);
    }

    /**
     * 添加菜单或者按钮
     *
     * @param menuDTO 菜单信息传输对昂
     * @param result  结果，绑定结果，校验结果
     * @return Result
     */
    @Override
    @ControllerEndpoint(exceptionMessage = "添加菜单/权限信息失败", operation = "菜单/权限管理[添加]")
    public Result submitMenu(@RequestBody MenuDTO menuDTO, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        return menuRepository.submitMenu(menuDTO);
    }

    /**
     * 删除菜单/按钮
     *
     * @param id 菜单/按钮 ID
     * @return Result
     */
    @Override
    @ControllerEndpoint(exceptionMessage = "删除菜单/权限信息失败", operation = "菜单/权限管理[删除]")
    public Result deleteMenu(Long id) {
        return menuRepository.deleteMenu(id);
    }

    /**
     * 导出菜单列表
     *
     * @param response 响应头
     * @param menuSearchForm 查询实体数据包
     */
    @Override
    @ControllerEndpoint(exceptionMessage = "导出菜单/权限信息失败", operation = "菜单/权限管理[导出]")
    public void exportMenuList(HttpServletResponse response, MenuSearchForm menuSearchForm) {
        menuRepository.exportMenuList(response, menuSearchForm);
    }

}

