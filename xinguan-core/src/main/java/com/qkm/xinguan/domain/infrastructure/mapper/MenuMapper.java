package com.qkm.xinguan.domain.infrastructure.mapper;

import com.qkm.xinguan.domain.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜单表 Mapper 接口
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 检查指定的菜单下是否含有子菜单
     *
     * @param menuId 菜单ID
     * @return Integer
     */
    Integer checkHasChildren(Long menuId);

    /**
     * 获取父级子项的资源id列表
     *
     * @param menuId 菜单ID
     * @return List<Long>
     */
    List<Long> getChildrenIds(Long menuId);

    /**
     * 根据父级的状态，更新子菜单状态，如果父级状态没变的话，则不更新子菜单状态
     *
     * @param ids       子菜单项的id列表
     * @param available 待更新的状态
     */
    void updateChildrens(List<Long> ids, Integer available);

    /**
     * 获取指定菜单ID的名称
     *
     * @param menuIds 菜单ID集合
     * @return List<String>
     */
    List<String> getMenuNameListByIds(List<Long> menuIds);
}
