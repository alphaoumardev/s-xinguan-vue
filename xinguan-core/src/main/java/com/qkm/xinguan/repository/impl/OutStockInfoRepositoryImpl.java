package com.qkm.xinguan.repository.impl;

import com.qkm.xinguan.domain.entity.OutStockInfo;
import com.qkm.xinguan.domain.infrastructure.mapper.OutStockInfoMapper;
import com.qkm.xinguan.repository.OutStockInfoRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Service
public class OutStockInfoRepositoryImpl extends ServiceImpl<OutStockInfoMapper, OutStockInfo> implements OutStockInfoRepository {

}
