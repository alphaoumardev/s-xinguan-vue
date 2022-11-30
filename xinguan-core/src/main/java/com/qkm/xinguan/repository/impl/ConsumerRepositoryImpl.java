package com.qkm.xinguan.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.entity.Consumer;
import com.qkm.xinguan.domain.infrastructure.mapper.ConsumerMapper;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.ConsumerRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import com.qkm.xinguan.vo.ConsumerVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 消费型 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Service
public class ConsumerRepositoryImpl extends ServiceImpl<ConsumerMapper, Consumer> implements ConsumerRepository {

    /**
     * 分页获取物资去向列表
     *
     * @param page       page
     * @param size       size
     * @param consumerVo 查询实体
     * @return Result
     */
    @Override
    public Result findConsumerList(Integer page, Integer size, ConsumerVo consumerVo) {
        Page<Consumer> pageInfo = this.baseMapper.selectPage(new Page<>(page, size), buildWrapper(consumerVo));
        return Result.ok().data(toConsumerVoPage(pageInfo));
    }

    /**
     * 添加物资去向
     *
     * @param consumerVo 数据
     * @return Result
     */
    @Override
    public Result add(ConsumerVo consumerVo) {
        Consumer consumer = new Consumer();
        BeanUtils.copyProperties(consumerVo, consumer);
        int res = this.baseMapper.insert(consumer);
        if (res > 0) {
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    /**
     * 更新物资去向信息
     *
     * @param id         id
     * @param consumerVo 数据
     * @return Result
     */
    @Override
    public Result update(Long id, ConsumerVo consumerVo) {
        Consumer consumer = this.baseMapper.selectById(id);
        if (Objects.isNull(consumer)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该供应商不存在，请重试！");
        }
        BeanUtils.copyProperties(consumerVo, consumer);
        int res = this.baseMapper.updateById(consumer);
        if (res > 0) {
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    /**
     * 删除物资去向
     *
     * @param id id
     * @return Result
     */
    @Override
    public Result delete(Long id) {
        Consumer consumer = this.baseMapper.selectById(id);
        if (Objects.isNull(consumer)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该供应商不存在，请重试！");
        }
        int res = this.baseMapper.deleteById(id);
        if (res > 0) {
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    /**
     * 查询所有的物资去向列表
     *
     * @return Result
     */
    @Override
    public List<ConsumerVo> findAll() {
        List<Consumer> consumers = this.baseMapper.selectList(null);
        return toConsumerVoList(consumers);
    }

    private static LambdaQueryWrapper<Consumer> buildWrapper(ConsumerVo consumerVo) {
        LambdaQueryWrapper<Consumer> wrapper = Wrappers.lambdaQuery(Consumer.class);
        if (!StringUtils.isEmpty(consumerVo.getName())) {
            wrapper.like(Consumer::getName, consumerVo.getName());
        }
        if (!StringUtils.isEmpty(consumerVo.getAddress())) {
            wrapper.like(Consumer::getAddress, consumerVo.getAddress());
        }
        if (!StringUtils.isEmpty(consumerVo.getContact())) {
            wrapper.like(Consumer::getContact, consumerVo.getContact());
        }
        wrapper.orderByAsc(Consumer::getSort);
        return wrapper;
    }

    private static List<ConsumerVo> toConsumerVoList(List<Consumer> consumers) {
        List<ConsumerVo> consumerVos = new ArrayList<>(consumers.size());
        consumers.forEach(consumer -> {
            ConsumerVo consumerVo = new ConsumerVo();
            BeanUtils.copyProperties(consumer, consumerVo);
            consumerVos.add(consumerVo);
        });
        return consumerVos;
    }

    private static Page<ConsumerVo> toConsumerVoPage(Page<Consumer> pageInfo) {
        Page<ConsumerVo> consumerVoPage = new Page<>();
        List<Consumer> records = pageInfo.getRecords();
        BeanUtils.copyProperties(pageInfo, consumerVoPage);
        consumerVoPage.setRecords(toConsumerVoList(records));
        return consumerVoPage;
    }
}
