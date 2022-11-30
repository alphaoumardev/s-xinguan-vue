package com.qkm.xinguan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.infrastructure.annotation.ControllerEndpoint;
import com.qkm.xinguan.domain.transform.form.InStockSearchForm;
import com.qkm.xinguan.repository.InStockRepository;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.InStockDetailVo;
import com.qkm.xinguan.vo.InStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 前端控制器
 *
 * @author qiukangming
 * @since 2020-09-24
 */

@RestController
public class InStockController implements IInStockController {

    private final InStockRepository inStockRepository;

    @Autowired
    public InStockController(InStockRepository inStockRepository) {
        this.inStockRepository = inStockRepository;
    }

    @Override
    public Result getInStockList(Integer page, Integer size, InStockSearchForm inStockSearchForm) {
        Page<InStockVo> pageInfo = inStockRepository.getInStockList(page, size, inStockSearchForm);
        return Result.ok().data(pageInfo);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "导出入库记录列表失败", operation = "入库记录[导出]")
    public void exportInStockList(HttpServletResponse response, InStockSearchForm searchForm) {
        inStockRepository.exportInStockList(response, searchForm);
    }

    @Override
    public Result getInStockDetailList(Long id, Integer page, Integer size) {
        InStockDetailVo detail = inStockRepository.getInStockDetailList(id, page, size);
        return Result.ok().data(detail);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "入库记录移入回收站失败", operation = "入库记录[移入回收站]")
    public Result remove(Long id) {
        return inStockRepository.remove(id);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "入库记录移出回收站失败", operation = "入库记录[移出回收站]")
    public Result back(Long id) {
        return inStockRepository.back(id);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "入库记录审核失败", operation = "入库记录[审核]")
    public Result publish(Long id) {
        return inStockRepository.push(id);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "入库记录删除失败", operation = "入库记录[删除]")
    public Result delete(Long id) {
        return inStockRepository.delete(id);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "入库记录添加失败", operation = "入库记录[添加]")
    public Result addInStock(InStockVo inStockVo) {
        return inStockRepository.addInStock(inStockVo);
    }

}

