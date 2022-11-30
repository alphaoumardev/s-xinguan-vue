package com.qkm.xinguan.repository;

import com.qkm.xinguan.domain.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.ProductVo;

/**
 * 产品 服务类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
public interface ProductRepository extends IService<Product> {

    /**
     * 获取物资资料数据
     *
     * @param page 第几页
     * @param size 一页几条数据
     * @param categorys 分类
     * @param filter 是否查询全部物资资料，为 true 的时候用于入库页面使用
     * @param productVo 查询条件
     * @return Result
     */
    Result findProductList(Integer page, Integer size, String categorys, Boolean filter, ProductVo productVo);

    /**
     * 获取库存列表数据
     *
     * @param page 第几页
     * @param size 一页几条数据
     * @param categorys 分类
     * @param productVo 查询条件
     * @return Result
     */
    Result findProductStocks(Integer page, Integer size, String categorys, ProductVo productVo);

    /**
     * 获取所有库存列表数据
     *
     * @param categorys 选择条件
     * @param productVo 查询条件
     * @return Result
     */
    Result findAllProductStocks(String categorys, ProductVo productVo);

    /**
     * 添加物资
     *
     * @param productVo 物资数据
     * @return Result
     */
    Result add(ProductVo productVo);

    /**
     * 更新物资
     *
     * @param productVo 数据对象
     * @return Result
     */
    Result update(ProductVo productVo);

    /**
     * 删除物资
     *
     * @param id id
     * @return Result
     */
    Result delete(Long id);

    /**
     * 移除物资
     *
     * @param id id
     * @return Result
     */
    Result remove(Long id);

    /**
     * 物资审核
     *
     * @param id id
     * @return Result
     */
    Result publish(Long id);

    /**
     * 物资恢复
     *
     * @param id id
     * @return Result
     */
    Result back(Long id);
}
