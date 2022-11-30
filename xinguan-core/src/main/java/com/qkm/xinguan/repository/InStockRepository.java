package com.qkm.xinguan.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.entity.InStock;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qkm.xinguan.domain.transform.form.InStockSearchForm;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.InStockDetailVo;
import com.qkm.xinguan.vo.InStockVo;

import javax.servlet.http.HttpServletResponse;

/**
 * 入库 服务类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
public interface InStockRepository extends IService<InStock> {

    /**
     * 分页获取入库记录
     *
     * @param page page
     * @param size size
     * @param inStockSearchForm 查询实体
     * @return Page<InStockVo>
     */
    Page<InStockVo> getInStockList(Integer page, Integer size, InStockSearchForm inStockSearchForm);

    /**
     * 导出入库记录列表信息
     *
     * @param response 请求头
     * @param searchForm 查询实体
     */
    void exportInStockList(HttpServletResponse response, InStockSearchForm searchForm);

    /**
     * 获取物资入库明细
     *
     * @param id 入库记录ID
     * @param page 第几页
     * @param size 一页几条数据
     * @return InStockDetailVo
     */
    InStockDetailVo getInStockDetailList(Long id, Integer page, Integer size);

    /**
     * 移入回收站
     *
     * @param id id
     * @return Result
     */
    Result remove(Long id);

    /**
     * 物资入库审核
     *
     * @param id id
     * @return Result
     */
    Result push(Long id);

    /**
     * 删除入库单
     *
     * @param id id号
     * @return Result
     */
    Result delete(Long id);

    /**
     * 移出回收站
     *
     * @param id id
     * @return Result
     */
    Result back(Long id);

    /**
     * 添加物资入库
     *
     * @param inStockVo 实体
     * @return Result
     */
    Result addInStock(InStockVo inStockVo);
}
