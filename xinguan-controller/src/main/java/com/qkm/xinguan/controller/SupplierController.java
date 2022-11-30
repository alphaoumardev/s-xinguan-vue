package com.qkm.xinguan.controller;

import com.qkm.xinguan.domain.entity.Supplier;
import com.qkm.xinguan.domain.infrastructure.annotation.ControllerEndpoint;
import com.qkm.xinguan.exception.BindingResultException;
import com.qkm.xinguan.repository.SupplierRepository;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.SupplierVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前端控制器
 *
 * @author qiukangming
 * @since 2020-09-24
 */

@RestController
public class SupplierController implements ISupplierController {

    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierController(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public Result findAll() {
        List<SupplierVo> supplierVos = supplierRepository.findAll();
        return Result.ok().data(supplierVos);
    }

    @Override
    public Result findSupplierList(Integer page, Integer size, SupplierVo supplierVo) {
        return supplierRepository.findSupplierList(page, size, supplierVo);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "物资来源添加失败", operation = "物资来源[添加]")
    public Result add(SupplierVo supplierVo, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        return supplierRepository.add(supplierVo);
    }

    @Override
    public Result edit(Long id) {
        Supplier supplier = supplierRepository.getById(id);
        return Result.ok().data(supplier);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "物资来源更新失败", operation = "物资来源[更新]")
    public Result update(Long id, SupplierVo supplierVo, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        return supplierRepository.update(id, supplierVo);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "物资来源删除失败", operation = "物资来源[删除]")
    public Result delete(Long id) {
        return supplierRepository.delete(id);
    }
}

