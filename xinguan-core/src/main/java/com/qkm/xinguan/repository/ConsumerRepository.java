package com.qkm.xinguan.repository;

import com.qkm.xinguan.domain.entity.Consumer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.ConsumerVo;

import java.util.List;

/**
 * 物资去向 服务类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
public interface ConsumerRepository extends IService<Consumer> {

    /**
     * 分页获取物资去向列表
     *
     * @param page page
     * @param size size
     * @param consumerVo 查询实体
     * @return Result
     */
    Result findConsumerList(Integer page, Integer size, ConsumerVo consumerVo);

    /**
     * 添加物资去向
     *
     * @param consumerVo 数据
     * @return Result
     */
    Result add(ConsumerVo consumerVo);

    /**
     * 更新物资去向信息
     *
     * @param id id
     * @param consumerVo 数据
     * @return Result
     */
    Result update(Long id, ConsumerVo consumerVo);

    /**
     * 删除物资去向
     *
     * @param id id
     * @return Result
     */
    Result delete(Long id);

    /**
     * 查询所有的物资去向列表
     *
     * @return Result
     */
    List<ConsumerVo> findAll();
}
