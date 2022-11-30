package com.qkm.xinguan.repository;

import com.qkm.xinguan.domain.entity.ProductCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.ProductCategoryTreeNodeVo;
import com.qkm.xinguan.vo.ProductCategoryVo;

import java.util.List;

/**
 * 产品种类 服务类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
public interface ProductCategoryRepository extends IService<ProductCategory> {

    /**
     * 获取物资分类列表
     *
     * @param page 第几页
     * @param size 一页几条数据
     * @param productCategoryVo 查询实体
     * @return Result
     */
    Result findProductCategoryList(Integer page, Integer size, ProductCategoryVo productCategoryVo);

    /**
     * 获取分类树
     *
     * @param page page
     * @param size size
     * @return Result
     */
    Result categoryTree(Integer page, Integer size);

    /**
     * 获取父级分类树
     *
     * @return Result
     */
    List<ProductCategoryTreeNodeVo> getParentCategoryTree();

    /**
     * 获取所有分类
     *
     * @return Result
     */
    List<ProductCategory> findAll();

    /**
     * 添加分类
     *
     * @param productCategoryVo 数据对象
     * @return Result
     */
    Result add(ProductCategoryVo productCategoryVo);

    /**
     * 根据ID查询
     *
     * @param id id
     * @return Result
     */
    Result findById(Long id);

    /**
     * 更新分类信息
     *
     * @param id id
     * @param productCategoryVo vo
     * @return Result
     */
    Result update(Long id, ProductCategoryVo productCategoryVo);

    /**
     * 根据ID删除
     *
     * @param id id
     * @return Result
     */
    Result delete(Long id);
}
