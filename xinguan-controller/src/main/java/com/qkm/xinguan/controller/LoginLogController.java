package com.qkm.xinguan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.entity.LoginLog;
import com.qkm.xinguan.domain.infrastructure.annotation.ControllerEndpoint;
import com.qkm.xinguan.domain.transform.form.LoginLogSearchForm;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.LoginLogRepository;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.LoginReportVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录日志表 前端控制器
 *
 * @author qiukangming
 * @since 2020-09-24
 */

@RestController
public class LoginLogController implements ILoginLogController {

    private final LoginLogRepository loginLogRepository;

    @Autowired
    public LoginLogController(LoginLogRepository loginLogRepository) {
        this.loginLogRepository = loginLogRepository;
    }

    @Override
    public Result searchLoginLogListPage(@RequestBody LoginLogSearchForm loginLogSearchForm) {
        Page<LoginLog> pageData = loginLogRepository.searchLoginLogListPage(loginLogSearchForm.getPage(), loginLogSearchForm.getSize(), loginLogSearchForm);
        return Result.ok().data(pageData);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "删除登录日志失败", operation = "登录日志[删除]")
    public Result deleteLoginLog(Long id) {
        boolean b = loginLogRepository.removeById(id);
        if (b) {
            return Result.ok().message("删除登录日志成功！");
        } else {
            throw new BusinessException("删除登录日志失败，请重试！");
        }
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "批量删除登录日志失败", operation = "登录日志[批量删除]")
    public Result batchDeleteLoginLog(String ids) {
        return loginLogRepository.batchDeleteLoginLog(ids);
    }

    @Override
    public Result loginReport(String username) {
        List<LoginReportVo> loginReportVosMe = loginLogRepository.loginReport(username);
        List<LoginReportVo> loginReportVosAll = loginLogRepository.loginReport(null);
        Map<String,Object> map = new HashMap<>(2);
        map.put("all", loginReportVosAll);
        map.put("me", loginReportVosMe);
        return Result.ok().data(map);
    }
}

