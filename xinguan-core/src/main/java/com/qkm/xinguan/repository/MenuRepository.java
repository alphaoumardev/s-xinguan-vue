package com.qkm.xinguan.repository;

import com.qkm.xinguan.domain.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qkm.xinguan.domain.transform.dto.MenuDTO;
import com.qkm.xinguan.domain.transform.form.MenuSearchForm;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.MenuNodeVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 菜单表 服务类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
public interface MenuRepository extends IService<Menu> {

    /**
     * 获取菜单和按钮
     *
     * @return List<MenuNodeVo>
     */
    List<MenuNodeVo> findMenuAndButton();

    /**
     * 仅获取菜单
     *
     * @return Result
     */
    Result findMenusAndUrl();

    /**
     * 获取需要展开的菜单 ID
     *
     * @return List<Integer>
     */
    List<Long> getOpenIds();

    /**
     * 更新菜单信息
     *
     * @param menuDTO 传输数据
     * @return Result
     */
    Result update(MenuDTO menuDTO);

    /**
     * 添加菜单/按钮信息
     *
     * @param menuDTO 传输数据
     * @return Result
     */
    Result submitMenu(MenuDTO menuDTO);

    /**
     * 删除菜单/按钮
     *
     * @param id 菜单/按钮 ID
     * @return Result
     */
    Result deleteMenu(Long id);

    /**
     * 导出菜单列表
     *
     * @param response 响应头
     * @param menuSearchForm 查询实体数据包
     */
    void exportMenuList(HttpServletResponse response, MenuSearchForm menuSearchForm);
}
