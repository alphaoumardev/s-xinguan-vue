package com.qkm.xinguan.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.entity.Health;
import com.qkm.xinguan.domain.infrastructure.annotation.ControllerEndpoint;
import com.qkm.xinguan.domain.infrastructure.security.LoginUser;
import com.qkm.xinguan.domain.infrastructure.utils.HttpUtil;
import com.qkm.xinguan.domain.infrastructure.utils.UserUtil;
import com.qkm.xinguan.domain.transform.dto.HealthDTO;
import com.qkm.xinguan.domain.transform.form.PointSearchForm;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.HealthRepository;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import com.qkm.xinguan.vo.HealthyVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 前端控制器
 *
 * @author qiukangming
 * @since 2020-09-24
 */

@Slf4j
@RestController
public class HealthController implements IHealthController {

    @Value("${yq.dataUrl}")
    private String yqDataUrl;

    private final HealthRepository healthRepository;

    @Autowired
    public HealthController(HealthRepository healthRepository) {
        this.healthRepository = healthRepository;
    }

    @Override
    public Result getYqData() {
        // 发起一个GET请求，获取数据
        try {
            String data = HttpUtil.httpGet(yqDataUrl);
            return Result.ok().data(JSONUtil.parseObj(data));
        } catch (Exception e) {
            log.error("发送 GET 请求发生异常[HttpClientUtils:httpGet]: " + e.getMessage());
            throw new BusinessException(ResultCode.API_GET_ERROR, e);
        }
    }

    @Override
    public Result isReport() {
        LoginUser loginUser = UserUtil.getCurrentLoginUser();
        assert loginUser != null;
        Health health = healthRepository.isReport(loginUser.getId());
        return Result.ok().data(health);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "健康上报失败", operation = "健康[上报]")
    public Result report(@RequestBody HealthDTO healthDTO) {
        LoginUser loginUser = UserUtil.getCurrentLoginUser();
        assert loginUser != null;
        healthDTO.setUserId(loginUser.getId());
        int res = healthRepository.report(healthDTO);
        if (res > 0) {
            return Result.ok();
        } else {
            throw new BusinessException(ResultCode.REPORT_ERROR);
        }
    }

    @Override
    public Result historyList(Integer page, Integer size) {
        LoginUser loginUser = UserUtil.getCurrentLoginUser();
        assert loginUser != null;
        Page<Health> list = healthRepository.historyList(loginUser.getId(), page, size);
        return Result.ok().data(list);
    }

    @Override
    public Result getPointsList(Integer page, Integer size, PointSearchForm pointSearchForm) {
        Page<HealthyVo> pageInfo = healthRepository.getPointsList(page, size, pointSearchForm);
        return Result.ok().data(pageInfo);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "删除打卡记录失败", operation = "打卡记录[删除]")
    public Result deletePoint(Long id) {
        return healthRepository.deletePoint(id);
    }

    @Override
    public void exportPointsList(HttpServletResponse response, PointSearchForm pointSearchForm) {
        healthRepository.exportPointsList(response, pointSearchForm);
    }
}

