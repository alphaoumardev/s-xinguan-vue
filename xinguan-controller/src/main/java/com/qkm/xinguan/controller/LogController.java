package com.qkm.xinguan.controller;

import com.qkm.xinguan.domain.transform.form.LogSearchForm;
import com.qkm.xinguan.repository.LogRepository;
import com.qkm.xinguan.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


/**
 * 操作日志表 前端控制器
 *
 * @author qiukangming
 * @since 2020-09-24
 */

@RestController
public class LogController implements ILogController {

    private final LogRepository logRepository;

    @Autowired
    public LogController(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public Result getSystemLogList(Integer page, Integer size, LogSearchForm logSearchForm) {
        return logRepository.getSystemLogList(page, size, logSearchForm);
    }

    @Override
    public Result delById(Long id) {
        return logRepository.delById(id);
    }

    @Override
    public Result batchDelById(Long[] ids) {
        return logRepository.batchDelById(ids);
    }
}

