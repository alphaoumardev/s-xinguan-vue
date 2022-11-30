package com.qkm.xinguan.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.entity.Supplier;
import com.qkm.xinguan.domain.infrastructure.mapper.SupplierMapper;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.SupplierRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import com.qkm.xinguan.vo.SupplierVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Service
public class SupplierRepositoryImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierRepository {

    /**
     * 获取所有供应商信息
     *
     * @return List<SupplierVo>
     */
    @Override
    public List<SupplierVo> findAll() {
        List<Supplier> suppliers = this.baseMapper.selectList(null);
        return toSupplierVoList(suppliers);
    }

    /**
     * 分页获取物资来源信息
     *
     * @param page       page
     * @param size       page
     * @param supplierVo 数据
     * @return Result
     */
    @Override
    public Result findSupplierList(Integer page, Integer size, SupplierVo supplierVo) {
        Page<Supplier> pageInfo = this.baseMapper.selectPage(new Page<>(page, size), buildWrapper(supplierVo));
        return Result.ok().data(pageInfo);
    }

    /**
     * 添加物资来源
     *
     * @param supplierVo vo
     * @return Result
     */
    @Override
    public Result add(SupplierVo supplierVo) {
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierVo, supplier);
        int res = this.baseMapper.insert(supplier);
        if (res > 0) {
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    /**
     * 更新信息
     *
     * @param id         id
     * @param supplierVo 数据
     * @return Result
     */
    @Override
    public Result update(Long id, SupplierVo supplierVo) {
        Supplier supplier = this.baseMapper.selectById(id);
        if (Objects.isNull(supplier)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "来源信息不存在，请重试！");
        }
        BeanUtils.copyProperties(supplierVo, supplier);
        int res = this.baseMapper.updateById(supplier);
        if (res > 0) {
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    /**
     * 根据ID删除
     *
     * @param id id
     * @return Result
     */
    @Override
    public Result delete(Long id) {
        Supplier supplier = this.baseMapper.selectById(id);
        if (Objects.isNull(supplier)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "来源信息不存在，请重试！");
        }
        int res = this.baseMapper.deleteById(id);
        if (res > 0) {
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    private static List<SupplierVo> toSupplierVoList(List<Supplier> suppliers) {
        List<SupplierVo> voList = new ArrayList<>(suppliers.size());
        suppliers.forEach(supplier -> {
            SupplierVo sv = new SupplierVo();
            BeanUtils.copyProperties(supplier, sv);
            voList.add(sv);
        });
        return voList;
    }

    private static LambdaQueryWrapper<Supplier> buildWrapper(SupplierVo supplierVo) {
        LambdaQueryWrapper<Supplier> wrapper = Wrappers.lambdaQuery(Supplier.class);
        if (!StringUtils.isEmpty(supplierVo.getName())) {
            wrapper.like(Supplier::getName, supplierVo.getName());
        }
        if (!StringUtils.isEmpty(supplierVo.getContact())) {
            wrapper.like(Supplier::getContact, supplierVo.getContact());
        }
        if (!StringUtils.isEmpty(supplierVo.getAddress())) {
            wrapper.like(Supplier::getAddress, supplierVo.getAddress());
        }
        wrapper.orderByAsc(Supplier::getSort);
        return wrapper;
    }
}
