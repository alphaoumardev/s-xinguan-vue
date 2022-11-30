package com.qkm.xinguan.domain.infrastructure.mapper;

import com.qkm.xinguan.domain.entity.OutStockInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author qiukangming
 * @description 出库单数据访问 Mapper 接口
 * @since 2020-09-24
 */
@Mapper
public interface OutStockInfoMapper extends BaseMapper<OutStockInfo> {

}
