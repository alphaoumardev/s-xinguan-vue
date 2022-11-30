package com.qkm.xinguan.domain.infrastructure.converter;

import com.qkm.xinguan.constant.SystemConst;
import com.qkm.xinguan.vo.MenuNodeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import com.qkm.xinguan.domain.entity.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/02/27 10:09
 * @description 菜单转换器 0 菜单 1 按钮
 */

public class MenuConverter {

    /**
     * Menu 实体转换成 MenuNodeVo 传输对象
     *
     * @param menus menus 实体
     * @return List<MenuNodeVo>
     */
    public static List<MenuNodeVo> converterToMenus(List<Menu> menus) {
        List<MenuNodeVo> menuNodeVos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(menus)) {
            menus.forEach(menu -> {
                MenuNodeVo menuNodeVo = new MenuNodeVo();
                BeanUtils.copyProperties(menu, menuNodeVo);
                menuNodeVo.setDisabled(menu.getAvailable().equals(SystemConst.FORBIDDEN));
                menuNodeVos.add(menuNodeVo);
            });
        }
        return menuNodeVos;
    }
}
