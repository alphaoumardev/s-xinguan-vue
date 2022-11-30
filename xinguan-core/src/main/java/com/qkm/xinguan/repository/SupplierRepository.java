package com.qkm.xinguan.repository;

import com.qkm.xinguan.domain.entity.Supplier;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.SupplierVo;

import java.util.List;

/**
 * 供应商 服务类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
public interface SupplierRepository extends IService<Supplier> {

    /**
     * 获取所有供应商信息
     *
     * @return List<SupplierVo>
     */
    List<SupplierVo> findAll();

    /**
     * 分页获取物资来源信息
     *
     * @param page page
     * @param size page
     * @param supplierVo 数据
     * @return Result
     */
    Result findSupplierList(Integer page, Integer size, SupplierVo supplierVo);

    /**
     * 添加物资来源
     *
     * @param supplierVo vo
     * @return Result
     */
    Result add(SupplierVo supplierVo);

    /**
     * 更新信息
     *
     * @param id id
     * @param supplierVo 数据
     * @return Result
     */
    Result update(Long id, SupplierVo supplierVo);

    /**
     * 根据ID删除
     *
     * @param id id
     * @return Result
     */
    Result delete(Long id);
}
