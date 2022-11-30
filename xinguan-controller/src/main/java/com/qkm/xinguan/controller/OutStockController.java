package com.qkm.xinguan.controller;

import com.qkm.xinguan.domain.infrastructure.annotation.ControllerEndpoint;
import com.qkm.xinguan.exception.BindingResultException;
import com.qkm.xinguan.repository.OutStockRepository;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.OutStockVo;
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
public class OutStockController implements IOutStockController {

    private final OutStockRepository outStockRepository;

    @Autowired
    public OutStockController(OutStockRepository outStockRepository) {
        this.outStockRepository = outStockRepository;
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "发放单申请失败", operation = "发放单[申请]")
    public Result addOutStock(OutStockVo outStockVo, BindingResult result) {
        if (result.hasErrors()){
            throw new BindingResultException(result);
        }
        return outStockRepository.addOutStock(outStockVo);
    }

    @Override
    public Result findInStockList(Integer page, Integer size, OutStockVo outStockVo) {
        return outStockRepository.findInStockList(page, size, outStockVo);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "发放单回收失败", operation = "发放单[回收]")
    public Result remove(Long id) {
        return outStockRepository.remove(id);
    }

    @Override
    public Result detail(Long id, Integer page, Integer size) {
        return outStockRepository.detail(id, page, size);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "发放单删除失败", operation = "发放单[删除]")
    public Result delete(Long id) {
        return outStockRepository.delete(id);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "发放单审核失败", operation = "发放单[审核]")
    public Result publish(Long id) {
        return outStockRepository.publish(id);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "发放单恢复失败", operation = "发放单[恢复]")
    public Result back(Long id) {
        return outStockRepository.back(id);
    }
}

