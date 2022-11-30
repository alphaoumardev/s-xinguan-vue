package com.qkm.xinguan.domain.infrastructure.mapper;

import com.qkm.xinguan.domain.entity.Consumer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author qiukangming
 * @description 消费者 Mapper 接口
 * @since 2020-09-24
 */
@Mapper
public interface ConsumerMapper extends BaseMapper<Consumer> {
}
