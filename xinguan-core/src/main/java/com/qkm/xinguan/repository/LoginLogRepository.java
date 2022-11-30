package com.qkm.xinguan.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.entity.LoginLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qkm.xinguan.domain.transform.form.LoginLogSearchForm;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.LoginReportVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 登录日志表 服务类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
public interface LoginLogRepository extends IService<LoginLog> {

    /**
     * 保存登录日志
     *
     * @param userName 登录用户名
     * @param request 信息
     */
    void saveLoginLog(String userName, HttpServletRequest request);

    /**
     * 分页查询登录日志信息
     *
     * @param page 第几页
     * @param size 一页几条数据
     * @param loginLogSearchForm 查询对象
     * @return Page<LoginLog>
     */
    Page<LoginLog> searchLoginLogListPage(Integer page, Integer size, LoginLogSearchForm loginLogSearchForm);

    /**
     * 批量删除登录日志
     *
     * @param ids id列表
     * @return Result
     */
    Result batchDeleteLoginLog(String ids);

    /**
     * 获取登录报表信息
     *
     * @param username 用户名
     * @return List<LoginReportVo>
     */
    List<LoginReportVo> loginReport(String username);
}
