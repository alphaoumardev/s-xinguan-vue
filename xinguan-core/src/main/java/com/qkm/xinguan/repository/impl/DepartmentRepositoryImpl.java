package com.qkm.xinguan.repository.impl;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.transform.form.DepartmentSearchForm;
import com.qkm.xinguan.domain.transform.dto.DepartmentSimpleDTO;
import com.qkm.xinguan.domain.entity.Department;
import com.qkm.xinguan.domain.infrastructure.mapper.DepartmentMapper;
import com.qkm.xinguan.domain.infrastructure.utils.ExcelStyleUtil;
import com.qkm.xinguan.domain.infrastructure.utils.ExcelUtil;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.DepartmentRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qkm.xinguan.response.ResultCode;
import com.qkm.xinguan.vo.DepartmentExportVo;
import com.qkm.xinguan.vo.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门管理服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Service
public class DepartmentRepositoryImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentRepository {

    @Override
    public List<DepartmentSimpleDTO> findDeptNameAndCount() {
        return this.baseMapper.findDeptNameAndCount();
    }

    @Override
    public String getDeptNameById(Long id) {
        return this.baseMapper.getDeptNameById(id);
    }

    @Override
    public List<String> getDeptNamesByIds(List<Long> ids) {
        return this.baseMapper.getDeptNamesByIds(ids);
    }

    @Override
    public Long getIdByDeptName(String deptName) {
        return this.baseMapper.getIdByDeptName(deptName);
    }

    /**
     * 获取全部部门信息
     *
     * @param searchForm 查询form
     * @return Result
     */
    @Override
    public Page<DepartmentVo> getListPage(DepartmentSearchForm searchForm) {
        LambdaQueryWrapper<Department> wrapper = executeWrapper(searchForm);
        Page<Department> page = this.baseMapper.selectPage(new Page<>(searchForm.getPage(), searchForm.getSize()), wrapper);
        return toDepartmentVoPage(page);
    }

    /**
     * 通过部门 id 获取部门总人数
     *
     * @param id id
     * @return Integer
     */
    @Override
    public Integer getDeptPersonsById(Long id) {
        return this.baseMapper.getDeptPersonsById(id);
    }

    /**
     * 导出部门列表
     *
     * @param response   头
     * @param searchForm 查询对象
     */
    @Override
    public void exportDeptList(HttpServletResponse response, DepartmentSearchForm searchForm) {
        LambdaQueryWrapper<Department> wrapper = executeWrapper(searchForm);
        List<Department> list = this.baseMapper.selectList(wrapper);
        List<DepartmentVo> voList = this.toDepartmentVoList(list);
        List<DepartmentExportVo> exportVos = new ArrayList<>(voList.size());
        voList.forEach(vo -> {
            DepartmentExportVo exportVo = new DepartmentExportVo();
            BeanUtils.copyProperties(vo, exportVo);
            exportVos.add(exportVo);
        });
        // 实现导出
        try {
            ExportParams params = new ExportParams("部门信息表格", "sheet1", ExcelType.XSSF);
            params.setStyle(ExcelStyleUtil.class);
            ExcelUtil.exportExcel(exportVos, DepartmentExportVo.class, "部门信息表", params, response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ResultCode.EXPORT_FAIL);
        }
    }

    private Page<DepartmentVo> toDepartmentVoPage(Page<Department> departmentPage) {
        Page<DepartmentVo> page = new Page<>();
        BeanUtils.copyProperties(departmentPage, page);
        List<DepartmentVo> list = toDepartmentVoList(departmentPage.getRecords());
        page.setRecords(list);
        return page;
    }

    private List<DepartmentVo> toDepartmentVoList(List<Department> departments) {
        List<DepartmentVo> departmentVos = new ArrayList<>(departments.size());
        departments.forEach(department -> {
            DepartmentVo departmentVo = new DepartmentVo();
            Integer count = this.getDeptPersonsById(department.getId());
            departmentVo.setTotal(count);
            BeanUtils.copyProperties(department, departmentVo);
            departmentVos.add(departmentVo);
        });
        return departmentVos;
    }

    private static LambdaQueryWrapper<Department> executeWrapper(DepartmentSearchForm searchForm) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(searchForm.getDeptName())) {
            wrapper.like(Department::getName, searchForm.getDeptName());
        }
        if (!StringUtils.isEmpty(searchForm.getPhone())) {
            wrapper.like(Department::getPhone, searchForm.getPhone());
        }
        return wrapper;
    }
}
