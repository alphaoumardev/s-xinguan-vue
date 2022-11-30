package com.qkm.xinguan.domain.infrastructure.utils;

import com.qkm.xinguan.constant.SystemConst;
import com.qkm.xinguan.vo.MenuNodeVo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/02/27 10:26
 * @description 递归构建树形菜单
 */
public class MenuTreeBuilderUtil {

    /**
     * 构建多级菜单
     *
     * @param menuNodes 菜单列表，数据库查询出来的
     * @return List<MenuNodeVo>
     */
    public static List<MenuNodeVo> build(List<MenuNodeVo> menuNodes) {
        // 获取根节点并且根据 Menu 类的 order 排序
        List<MenuNodeVo> rootMenus = menuNodes.stream().filter(menu -> menu.getParentId().equals(SystemConst.ROOT_MENU)).sorted(MenuNodeVo.order()).collect(Collectors.toList());

        // 为根菜单设置字菜单 使用 getChild 方法
        rootMenus.forEach(root -> {
            List<MenuNodeVo> child = getChild(root.getId(), menuNodes);
            root.setChildren(child);
        });

        // 返回分级之后的菜单
        return rootMenus;
    }

    /**
     * 获取子菜单
     *
     * @param id    菜单id
     * @param nodes 菜单节点列表
     * @return List<MenuNodeVo>
     */
    private static List<MenuNodeVo> getChild(Long id, List<MenuNodeVo> nodes) {
        // 子菜单
        List<MenuNodeVo> childList = nodes.stream().filter(node -> node.getParentId().equals(id)).collect(Collectors.toList());
        // 递归
        childList.forEach(child -> child.setChildren(getChild(child.getId(), nodes)));
        // 排序
        childList.sort(MenuNodeVo.order());
        // 如果节点下没有子节点，返回一个空List（递归退出）
        if (childList.isEmpty()) {
            return new ArrayList<>();
        }
        return childList;
    }
}
