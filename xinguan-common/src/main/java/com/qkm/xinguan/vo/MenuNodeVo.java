package com.qkm.xinguan.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/02/27 10:00
 * @description
 */

@Data
public class MenuNodeVo {
    private Long id;

    private Long parentId;

    private String menuName;

    private String url = null;

    private String icon;

    private Long orderNum;

    private Integer open;

    private boolean disabled;

    private String perms;

    private String type;


    private List<MenuNodeVo> children = new ArrayList<>();

    /*
     * 排序,根据order排序
     */
    public static Comparator<MenuNodeVo> order() {
        return (o1, o2) -> {
            if (!o1.getOrderNum().equals(o2.getOrderNum())) {
                return (int) (o1.getOrderNum() - o2.getOrderNum());
            }
            return 0;
        };
    }
}
