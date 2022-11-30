package com.qkm.xinguan.controller;

import com.qkm.xinguan.domain.entity.Product;
import com.qkm.xinguan.domain.infrastructure.annotation.ControllerEndpoint;
import com.qkm.xinguan.exception.BindingResultException;
import com.qkm.xinguan.repository.ProductRepository;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.ProductVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前端控制器
 *
 * @author qiukangming
 * @since 2020-09-24
 */

@RestController
public class ProductController implements IProductController {

    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Result findProductList(Integer page, Integer size, String categorys, Boolean filter, ProductVo productVo) {
        return productRepository.findProductList(page, size, categorys, filter, productVo);
    }

    @Override
    public Result findProductStocks(Integer page, Integer size, String categorys, ProductVo productVo) {
        return productRepository.findProductStocks(page, size, categorys, productVo);
    }

    @Override
    public Result findAllProductStocks(String categorys, ProductVo productVo) {
        return productRepository.findAllProductStocks(categorys, productVo);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "添加物资失败", operation = "物资资料[添加]")
    public Result add(ProductVo productVo, BindingResult result) {
        if (result.hasErrors()){
            throw new BindingResultException(result);
        }
        return productRepository.add(productVo);
    }

    @Override
    public Result findById(Long id) {
        Product product = productRepository.getById(id);
        ProductVo productVo = new ProductVo();
        BeanUtils.copyProperties(product, productVo);
        return Result.ok().data(productVo);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "更新物资失败", operation = "物资资料[更新]")
    public Result update(Long id, ProductVo productVo, BindingResult result) {
        if (result.hasErrors()){
            throw new BindingResultException(result);
        }
        return productRepository.update(productVo);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "删除物资失败", operation = "物资资料[删除]")
    public Result delete(Long id) {
        return productRepository.delete(id);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "回收物资失败", operation = "物资资料[回收]")
    public Result remove(Long id) {
        return productRepository.remove(id);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "物资添加审核失败", operation = "物资资料[审核]")
    public Result publish(Long id) {
        return productRepository.publish(id);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "恢复物资失败", operation = "物资资料恢复")
    public Result back(Long id) {
        return productRepository.back(id);
    }
}

