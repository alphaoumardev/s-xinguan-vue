package com.qkm.xinguan.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.transform.form.DepartmentSearchForm;
import com.qkm.xinguan.domain.transform.dto.DepartmentSimpleDTO;
import com.qkm.xinguan.domain.entity.Department;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qkm.xinguan.vo.DepartmentVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 部门 服务类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
public interface DepartmentRepository extends IService<Department> {

    /**
     * 获取部门信息，包括包含的人数
     *
     * @return List<DepartmentSimple>
     */
    List<DepartmentSimpleDTO> findDeptNameAndCount();

    /**
     * 通过 部门id获取部门名称
     *
     * @return String
     */
    String getDeptNameById(Long id);

    /**
     * 通过 部门id列表获取部门名称列表
     *
     * @return List<String>
     */
    List<String> getDeptNamesByIds(List<Long> ids);

    /**
     * 通过 部门名称获取部门id
     *
     * @return String
     */
    Long getIdByDeptName(String deptName);

    /**
     * 获取全部部门信息
     *
     * @param searchForm 查询form
     * @return Page<Department>
     */
    Page<DepartmentVo> getListPage(DepartmentSearchForm searchForm);

    /**
     * 通过部门 id 获取部门总人数
     *
     * @param id id
     * @return Integer
     */
    Integer getDeptPersonsById(Long id);

    /**
     * 导出部门列表
     *
     * @param response   头
     * @param searchForm 查询对象
     */
    void exportDeptList(HttpServletResponse response, DepartmentSearchForm searchForm);
}
