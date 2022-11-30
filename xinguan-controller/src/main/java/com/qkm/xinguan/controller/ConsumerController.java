package com.qkm.xinguan.controller;

import com.qkm.xinguan.domain.entity.Consumer;
import com.qkm.xinguan.domain.infrastructure.annotation.ControllerEndpoint;
import com.qkm.xinguan.exception.BindingResultException;
import com.qkm.xinguan.repository.ConsumerRepository;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.ConsumerVo;
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
public class ConsumerController implements IConsumerController {

    private final ConsumerRepository consumerRepository;

    @Autowired
    public ConsumerController(ConsumerRepository consumerRepository) {
        this.consumerRepository = consumerRepository;
    }

    @Override
    public Result findConsumerList(Integer page, Integer size, ConsumerVo consumerVo) {
        return consumerRepository.findConsumerList(page, size, consumerVo);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "物资去向添加失败", operation = "物资去向[添加]")
    public Result add(ConsumerVo consumerVo, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        return consumerRepository.add(consumerVo);
    }

    @Override
    public Result findById(Long id) {
        Consumer entity = consumerRepository.getById(id);
        return Result.ok().data(entity);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "物资去向更新失败", operation = "物资去向[更新]")
    public Result update(Long id, ConsumerVo consumerVo, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        return consumerRepository.update(id, consumerVo);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "物资去向删除失败", operation = "物资去向[删除]")
    public Result delete(Long id) {
        return consumerRepository.delete(id);
    }

    @Override
    public Result findAll() {
        List<ConsumerVo> list = consumerRepository.findAll();
        return Result.ok().data(list);
    }
}

