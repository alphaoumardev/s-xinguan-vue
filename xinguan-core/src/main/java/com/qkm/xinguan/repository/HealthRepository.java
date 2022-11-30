package com.qkm.xinguan.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.entity.Health;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qkm.xinguan.domain.transform.dto.HealthDTO;
import com.qkm.xinguan.domain.transform.form.PointSearchForm;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.HealthyVo;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * 健康 服务类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
public interface HealthRepository extends IService<Health> {

    /**
     * 根据用户ID获取健康记录
     *
     * @param userId 用户ID
     * @return Health
     */
    Health isReport(Long userId);

    /**
     * 健康记录上报
     *
     * @param healthDTO 传输数据
     * @return int
     */
    int report(HealthDTO healthDTO);

    /**
     * 获取打卡历史记录
     *
     * @param userId 当前登录用户ID
     * @param page   第几页
     * @param size   一页几条数据
     * @return Page<Health>
     */
    Page<Health> historyList(Long userId, Integer page, Integer size);

    /**
     * 打卡记录管理页面获取打卡列表
     *
     * @param page            第几页
     * @param size            一页几条数据
     * @param pointSearchForm 查询对象
     * @return Page<HealthyVo>
     */
    Page<HealthyVo> getPointsList(Integer page, Integer size, PointSearchForm pointSearchForm);

    /**
     * 删除打卡记录
     *
     * @param id ID
     * @return Result
     */
    Result deletePoint(Long id);

    /**
     * 打卡记录导出
     *
     * @param response 响应头
     * @param pointSearchForm 查询数据
     */
    void exportPointsList(HttpServletResponse response, PointSearchForm pointSearchForm);
}
