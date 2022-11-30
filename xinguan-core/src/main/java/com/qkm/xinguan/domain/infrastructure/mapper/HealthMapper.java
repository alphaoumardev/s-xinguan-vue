package com.qkm.xinguan.domain.infrastructure.mapper;

import com.qkm.xinguan.domain.entity.Health;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper 接口
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Mapper
public interface HealthMapper extends BaseMapper<Health> {

    /**
     * 根据用户ID获取健康记录
     *
     * @param userId        用户ID
     * @return Health
     */
    List<Health> isReport(Long userId);
}
