package com.qkm.xinguan.domain.infrastructure.mapper;

import com.qkm.xinguan.domain.entity.Log;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志表 Mapper 接口
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Mapper
public interface LogMapper extends BaseMapper<Log> {

}
