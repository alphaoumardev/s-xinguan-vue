package com.qkm.xinguan.repository;

import com.qkm.xinguan.domain.entity.Log;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qkm.xinguan.domain.transform.form.LogSearchForm;
import com.qkm.xinguan.response.Result;
import org.springframework.scheduling.annotation.Async;


/**
 * 操作日志表 服务类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
public interface LogRepository extends IService<Log> {

    /**
     * 异步保存操作日志
     */
    @Async("CodeAsyncThreadPool")
    void saveLog(Log log);

    /**
     * 获取系统日志操作记录列表
     *
     * @param page 第几页
     * @param size 一页数据
     * @param logSearchForm 查询条件
     * @return Result
     */
    Result getSystemLogList(Integer page, Integer size, LogSearchForm logSearchForm);

    /**
     * 根据ID删除
     *
     * @param id id
     * @return Result
     */
    Result delById(Long id);

    /**
     * 批量删除操作记录
     *
     * @param ids ids列表
     * @return Result
     */
    Result batchDelById(Long[] ids);
}
