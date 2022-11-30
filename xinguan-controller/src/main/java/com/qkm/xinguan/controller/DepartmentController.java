package com.qkm.xinguan.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.infrastructure.annotation.ControllerEndpoint;
import com.qkm.xinguan.domain.transform.dto.DepartmentDTO;
import com.qkm.xinguan.domain.transform.form.DepartmentSearchForm;
import com.qkm.xinguan.domain.transform.dto.DepartmentSimpleDTO;
import com.qkm.xinguan.domain.entity.Department;
import com.qkm.xinguan.exception.BindingResultException;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import com.qkm.xinguan.repository.DepartmentRepository;
import com.qkm.xinguan.vo.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 部门表, 前端控制器, 部门相关接口
 *
 * @author qiukangming
 * @since 2020-09-24
 */

@RestController
public class DepartmentController implements IDepartmentController {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Result getSelectList() {
        List<DepartmentSimpleDTO> dncList = departmentRepository.findDeptNameAndCount();
        if (dncList != null && !dncList.isEmpty()) {
            return Result.ok().data(dncList);
        } else {
            throw new BusinessException(ResultCode.DEPARTMENT_NOT_EXIST);
        }
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "根据部门名称获取ID失败", operation = "部门管理[查询]")
    public Result getIdByName(String deptName) {
        Long id = departmentRepository.getIdByDeptName(deptName);
        if (id != null) {
            return Result.ok().data(id);
        } else {
            throw new BusinessException(ResultCode.DEPARTMENT_NOT_EXIST);
        }
    }

    @Override
    public Result getList(@RequestBody DepartmentSearchForm searchForm, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        Page<DepartmentVo> page = departmentRepository.getListPage(searchForm);
        return Result.ok().data(page);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "添加部门信息失败", operation = "部门管理[添加]")
    public Result add(DepartmentDTO departmentDTO, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        Department department = new Department();
        BeanUtils.copyProperties(departmentDTO, department);
        boolean b = departmentRepository.save(department);
        if (b) {
            return Result.ok().message("添加部门成功");
        } else {
            throw new BusinessException(ResultCode.ADD_DEPT.getCode(), "添加部门信息失败");
        }
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "更新部门信息失败", operation = "部门管理[更新]")
    public Result update(DepartmentDTO departmentDTO, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        Department department = new Department();
        BeanUtils.copyProperties(departmentDTO, department);
        boolean b = departmentRepository.updateById(department);
        if (b) {
            return Result.ok().message("修改部门信息成功");
        } else {
            throw new BusinessException(ResultCode.EDIT_DEPT.getCode(), "修改部门信息失败");
        }
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "删除部门信息失败", operation = "部门管理[删除]")
    public Result delete(Long id) {
        Integer count = departmentRepository.getDeptPersonsById(id);
        if (count > 0) {
            throw new BusinessException(ResultCode.DEL_DEPT_NOT_EMPTY);
        }
        boolean b = departmentRepository.removeById(id);
        if (b) {
            return Result.ok().message("删除部门信息成功");
        } else {
            throw new BusinessException(ResultCode.FAILED.getCode(), "删除部门信息失败");
        }
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "导出部门信息列表失败", operation = "部门管理[导出]")
    public void exportUserList(HttpServletResponse response, DepartmentSearchForm searchForm, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        departmentRepository.exportDeptList(response, searchForm);
    }
}

