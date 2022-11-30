package com.qkm.xinguan.domain.infrastructure.mapper;

import com.qkm.xinguan.domain.entity.LoginLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qkm.xinguan.vo.LoginReportVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 登录日志表 Mapper 接口
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {

    /**
     * 获取登录报表信息
     *
     * @param username 用户名
     * @return List<LoginReportVo>
     */
    List<LoginReportVo> loginReport(String username);

}
