package com.qkm.xinguan.repository;

import com.qkm.xinguan.domain.entity.OutStock;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.OutStockVo;

/**
 * 出库 服务类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
public interface OutStockRepository extends IService<OutStock> {

    /**
     * 添加出库记录
     *
     * @param outStockVo 数据
     * @return Result
     */
    Result addOutStock(OutStockVo outStockVo);

    /**
     * 分页获取出库记录列表
     *
     * @param page page
     * @param size size
     * @param outStockVo 数据
     * @return Result
     */
    Result findInStockList(Integer page, Integer size, OutStockVo outStockVo);

    /**
     * 移入回收站
     *
     * @param id id
     * @return Result
     */
    Result remove(Long id);

    /**
     * 获取入库单明细
     *
     * @param id id
     * @param page page
     * @param size size
     * @return Result
     */
    Result detail(Long id, Integer page, Integer size);

    /**
     * 删除入库记录
     *
     * @param id id
     * @return Result
     */
    Result delete(Long id);

    /**
     * 入库审核
     *
     * @param id id
     * @return Result
     */
    Result publish(Long id);

    /**
     * 从回收站收回数据
     *
     * @param id id
     * @return Result
     */
    Result back(Long id);
}
