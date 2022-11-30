package com.qkm.xinguan.repository.impl;

import com.qkm.xinguan.domain.entity.ProductStock;
import com.qkm.xinguan.domain.infrastructure.mapper.ProductStockMapper;
import com.qkm.xinguan.repository.ProductStockRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Service
public class ProductStockRepositoryImpl extends ServiceImpl<ProductStockMapper, ProductStock> implements ProductStockRepository {

}
