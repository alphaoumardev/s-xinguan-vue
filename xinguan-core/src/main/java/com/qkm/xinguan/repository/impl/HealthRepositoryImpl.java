package com.qkm.xinguan.repository.impl;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.entity.Health;
import com.qkm.xinguan.domain.infrastructure.mapper.HealthMapper;
import com.qkm.xinguan.domain.infrastructure.mapper.UserMapper;
import com.qkm.xinguan.domain.infrastructure.utils.ExcelStyleUtil;
import com.qkm.xinguan.domain.infrastructure.utils.ExcelUtil;
import com.qkm.xinguan.domain.transform.dto.HealthDTO;
import com.qkm.xinguan.domain.transform.form.PointSearchForm;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.HealthRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import com.qkm.xinguan.vo.HealthyExportVo;
import com.qkm.xinguan.vo.HealthyVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 健康 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Service
public class HealthRepositoryImpl extends ServiceImpl<HealthMapper, Health> implements HealthRepository {

    private final UserMapper userMapper;

    @Autowired
    public HealthRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 根据用户ID获取健康记录
     *
     * @param userId        用户ID
     * @return Health
     */
    @Override
    public Health isReport(Long userId) {
        List<Health> list = this.baseMapper.isReport(userId);
        if (list.isEmpty()) return null;
        else return list.get(0);
    }

    /**
     * 健康记录上报
     *
     * @param healthDTO 传输数据
     * @return boolean
     */
    @Override
    public int report(HealthDTO healthDTO) {
        Health health = this.isReport(healthDTO.getUserId());
        if (!Objects.isNull(health)) {
            throw new BusinessException(ResultCode.DOUBLE_REPORT);
        }
        Health newHealth = new Health();
        BeanUtils.copyProperties(healthDTO, newHealth);
        return this.baseMapper.insert(newHealth);
    }

    /**
     * 获取打卡历史记录
     *
     * @param userId 当前登录用户ID
     * @param page   第几页
     * @param size   一页几条数据
     * @return Page<Health>
     */
    @Override
    public Page<Health> historyList(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Health> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Health::getUserId, userId);
        wrapper.orderByDesc(Health::getCreateTime);
        return this.baseMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 打卡记录管理页面获取打卡列表
     *
     * @param page            第几页
     * @param size            一页几条数据
     * @param pointSearchForm 查询对象
     * @return Page<HealthyVo>
     */
    @Override
    public Page<HealthyVo> getPointsList(Integer page, Integer size, PointSearchForm pointSearchForm) {
        Page<Health> pageInfo = this.baseMapper.selectPage(new Page<>(page, size), buildWrapper(pointSearchForm));
        return toVoPage(pageInfo);
    }

    /**
     * 删除打卡记录
     *
     * @param id ID
     * @return Result
     */
    @Override
    public Result deletePoint(Long id) {
        Health health = this.baseMapper.selectById(id);
        if (Objects.isNull(health)) {
            throw new BusinessException(ResultCode.FAILED, "打卡记录不存在，请刷新重试！");
        }
        int res = this.baseMapper.deleteById(id);
        if (res > 0){
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    /**
     * 打卡记录导出
     *
     * @param response        响应头
     * @param pointSearchForm 查询数据
     */
    @Override
    public void exportPointsList(HttpServletResponse response, PointSearchForm pointSearchForm) {
        List<Health> healthy = this.baseMapper.selectList(buildWrapper(pointSearchForm));
        List<HealthyExportVo> exportVos = toHealthExportVoList(toVoList(healthy));
        // 实现导出
        try {
            ExportParams params = new ExportParams("用户打卡信息表格", "sheet1", ExcelType.XSSF);
            params.setStyle(ExcelStyleUtil.class);
            ExcelUtil.exportExcel(exportVos, HealthyExportVo.class, "用户打卡信息表格", params, response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ResultCode.EXPORT_FAIL);
        }
    }

    private List<HealthyExportVo> toHealthExportVoList(List<HealthyVo> toVoList) {
        List<HealthyExportVo> healthyExportVos = new ArrayList<>(toVoList.size());
        toVoList.forEach(healthyVo -> {
            HealthyExportVo healthyExportVo = new HealthyExportVo();
            BeanUtils.copyProperties(healthyVo, healthyExportVo);
            healthyExportVo.setSituation(getTagString("situation", healthyVo.getSituation()));
            healthyExportVo.setTouch(getTagString("touch", healthyVo.getTouch()));
            healthyExportVo.setPassby(getTagString("passby", healthyVo.getPassby()));
            healthyExportVo.setReception(getTagString("reception", healthyVo.getReception()));
            healthyExportVos.add(healthyExportVo);
        });
        return healthyExportVos;
    }

    private LambdaQueryWrapper<Health> buildWrapper(PointSearchForm pointSearchForm) {
        LambdaQueryWrapper<Health> wrapper = Wrappers.lambdaQuery(Health.class);
        if (!StringUtils.isEmpty(pointSearchForm.getOrigin())) {
            String address = String.format("%s/%s/%s", pointSearchForm.getProvince(), pointSearchForm.getCity(), pointSearchForm.getOrigin());
            wrapper.eq(Health::getAddress, address);
        }
        else if (!StringUtils.isEmpty(pointSearchForm.getCity())) {
            String address = String.format("%s/%s/", pointSearchForm.getProvince(), pointSearchForm.getCity());
            wrapper.likeRight(Health::getAddress, address);
        }
        else if (!StringUtils.isEmpty(pointSearchForm.getProvince())) {
            String address = String.format("%s/", pointSearchForm.getProvince());
            wrapper.likeRight(Health::getAddress, address);
        }

        if (!StringUtils.isEmpty(pointSearchForm.getUsername())) {
            // 获取ID，这里查询的逻辑是，如果有用户ID，则根据用户ID匹配，如果没有该用户，则ID为null
            Long id = userMapper.getIdByUsername(pointSearchForm.getUsername());
            if (Objects.isNull(id)) {
                wrapper.isNull(Health::getUserId);
            } else {
                wrapper.eq(Health::getUserId, id);
            }
        }

        if (!Objects.isNull(pointSearchForm.getSituation())) {
            wrapper.eq(Health::getSituation, pointSearchForm.getSituation());
        }
        if (!Objects.isNull(pointSearchForm.getTouch())) {
            wrapper.eq(Health::getTouch, pointSearchForm.getTouch());
        }
        if (!Objects.isNull(pointSearchForm.getPassby())) {
            wrapper.eq(Health::getPassby, pointSearchForm.getPassby());
        }
        if (!Objects.isNull(pointSearchForm.getReception())) {
            wrapper.eq(Health::getReception, pointSearchForm.getReception());
        }

        if (!Objects.isNull(pointSearchForm.getStartTime()) && !Objects.isNull(pointSearchForm.getEndTime())) {
            wrapper.between(Health::getCreateTime, pointSearchForm.getStartTime(), pointSearchForm.getEndTime());
        }

        return wrapper;
    }

    private List<HealthyVo> toVoList(List<Health> list) {
        List<HealthyVo> healthyVos = new ArrayList<>(list.size());
        list.forEach(healthy -> {
            HealthyVo healthyVo = new HealthyVo();
            BeanUtils.copyProperties(healthy, healthyVo);
            healthyVo.setUsername(userMapper.getUsername(healthy.getUserId()));
            healthyVos.add(healthyVo);
        });
        return healthyVos;
    }

    private Page<HealthyVo> toVoPage(Page<Health> pageInfo) {
        Page<HealthyVo> voPage = new Page<>();
        BeanUtils.copyProperties(pageInfo, voPage);
        voPage.setRecords(toVoList(pageInfo.getRecords()));
        return voPage;
    }

    private static String getTagString(String type, Integer value) {
        if (type.equals("situation")) {
            switch (value) {
                case 0:
                    return "健康";
                case 1:
                    return "有咳嗽发热症状";
                case 2:
                    return "其他情况";
            }
        } else {
            switch (value) {
                case 0:
                    return "是";
                case 1:
                    return "否";
            }
        }
        return "Type Error";
    }
}
