package com.qkm.xinguan.repository.impl;

import com.qkm.xinguan.domain.entity.InStockInfo;
import com.qkm.xinguan.domain.infrastructure.mapper.InStockInfoMapper;
import com.qkm.xinguan.repository.InStockInfoRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 入库信息 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Service
public class InStockInfoRepositoryImpl extends ServiceImpl<InStockInfoMapper, InStockInfo> implements InStockInfoRepository {

}
