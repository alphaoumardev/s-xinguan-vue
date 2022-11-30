package com.qkm.xinguan.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/04 11:56
 * @description
 */

@Data
public class ProductCategoryTreeNodeVo {
    private Long id;

    private String name;

    private String remark;

    private Integer sort;

    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;

    private Long pid;

    private int lev;

    private List<ProductCategoryTreeNodeVo> children;

    /*
     * 排序,根据order排序
     */
    public static Comparator<ProductCategoryTreeNodeVo> order(){
        return (o1, o2) -> {
            if(!o1.getSort().equals(o2.getSort())){
                return o1.getSort() - o2.getSort();
            }
            return 0;
        };
    }
}
