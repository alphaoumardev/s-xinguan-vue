package com.qkm.xinguan.controller;

import com.qkm.xinguan.domain.entity.ProductCategory;
import com.qkm.xinguan.domain.infrastructure.annotation.ControllerEndpoint;
import com.qkm.xinguan.repository.ProductCategoryRepository;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.ProductCategoryTreeNodeVo;
import com.qkm.xinguan.vo.ProductCategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前端控制器
 *
 * @author qiukangming
 * @since 2020-09-24
 */

@RestController
public class ProductCategoryController implements IProductCategoryController {

    private final ProductCategoryRepository productCategoryRepository;

    @Autowired
    public ProductCategoryController(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    public Result findProductCategoryList(Integer page, Integer size, ProductCategoryVo productCategoryVo) {
        return productCategoryRepository.findProductCategoryList(page, size, productCategoryVo);
    }

    @Override
    public Result categoryTree(Integer page, Integer size) {
        return productCategoryRepository.categoryTree(page, size);
    }

    @Override
    public Result getParentCategoryTree() {
        List<ProductCategoryTreeNodeVo> parentTreeNode = productCategoryRepository.getParentCategoryTree();
        return Result.ok().data(parentTreeNode);
    }

    @Override
    public Result findAll() {
        List<ProductCategory> list = productCategoryRepository.findAll();
        return Result.ok().data(list);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "物资分类添加失败", operation = "物资分类[添加]")
    public Result add(ProductCategoryVo productCategoryVo) {
        return productCategoryRepository.add(productCategoryVo);
    }

    @Override
    public Result findById(Long id) {
        return productCategoryRepository.findById(id);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "物资分类更新失败", operation = "物资分类[更新]")
    public Result update(Long id, ProductCategoryVo productCategoryVo) {
        return productCategoryRepository.update(id, productCategoryVo);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "物资分类删除失败", operation = "物资分类[删除]")
    public Result delete(Long id) {
        return productCategoryRepository.delete(id);
    }
}

