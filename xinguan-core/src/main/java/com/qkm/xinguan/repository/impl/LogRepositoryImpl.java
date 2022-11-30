package com.qkm.xinguan.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.entity.Log;
import com.qkm.xinguan.domain.infrastructure.mapper.LogMapper;
import com.qkm.xinguan.domain.transform.form.LogSearchForm;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.LogRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 操作日志表 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Service
public class LogRepositoryImpl extends ServiceImpl<LogMapper, Log> implements LogRepository {

    /**
     * 异步保存操作日志
     *
     * @param log 日志信息
     */
    @Override
    public void saveLog(Log log) {
        this.baseMapper.insert(log);
    }

    /**
     * 获取系统日志操作记录列表
     *
     * @param logSearchForm 查询条件
     * @return Result
     */
    @Override
    public Result getSystemLogList(Integer page, Integer size, LogSearchForm logSearchForm) {
        Page<Log> pageInfo = this.baseMapper.selectPage(new Page<>(page, size), buildLambdaQueryWrapper(logSearchForm));
        return Result.ok().data(pageInfo);
    }

    /**
     * 根据ID删除
     *
     * @param id id
     * @return Result
     */
    @Override
    public Result delById(Long id) {
        Log log = this.baseMapper.selectById(id);
        if (Objects.isNull(log)){
            return Result.error(ResultCode.FAILED);
        }
        int res = this.baseMapper.deleteById(id);
        if (res > 0){
            return Result.ok();
        } else {
            throw new BusinessException("删除失败，请重试！");
        }
    }

    /**
     * 批量删除操作记录
     *
     * @param ids ids列表
     * @return Result
     */
    @Override
    public Result batchDelById(Long[] ids) {
        if (ids.length == 0){
            return Result.error();
        }
        int res = this.baseMapper.deleteBatchIds(Arrays.asList(ids));
        if (res > 0 && res == ids.length){
            return Result.ok();
        } else if (res > 0 && res < ids.length) {
            return Result.error().message("批量删除部分执行成功，请刷新重试！");
        } else {
            return Result.error().message("删除失败，请重试！");
        }
    }

    private static LambdaQueryWrapper<Log> buildLambdaQueryWrapper(LogSearchForm logSearchForm){
        LambdaQueryWrapper<Log> wrapper = Wrappers.lambdaQuery();
        if (!StringUtils.isEmpty(logSearchForm.getIp())){
            wrapper.eq(Log::getIp, logSearchForm.getIp());
        }
        if (!StringUtils.isEmpty(logSearchForm.getUsername())){
            wrapper.like(Log::getUsername, logSearchForm.getUsername());
        }
        if (!StringUtils.isEmpty(logSearchForm.getLocation())){
            wrapper.like(Log::getLocation, logSearchForm.getLocation());
        }
        wrapper.orderByDesc(Log::getCreateTime);
        return wrapper;
    }
}
