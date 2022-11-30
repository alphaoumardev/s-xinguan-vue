package com.qkm.xinguan.domain.infrastructure.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qkm.xinguan.vo.ProductStockVo;
import com.qkm.xinguan.vo.ProductVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper 接口
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    List<ProductStockVo> findProductStocks(Integer page, Integer size, ProductVo productVo);

    long findProductStocksCount(ProductVo productVo);

    List<ProductStockVo> findAllProductStocks(ProductVo productVo);
}
