package com.qkm.xinguan.domain.infrastructure.mapper;

import com.qkm.xinguan.domain.transform.form.DepartmentSearchForm;
import com.qkm.xinguan.domain.transform.dto.DepartmentSimpleDTO;
import com.qkm.xinguan.domain.entity.Department;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Department Mapper 接口
 *
 * @author qiukangming
 * @since 2020-09-24
 */

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
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
     * 通过 部门名称获取部门id
     *
     * @return String
     */
    Long getIdByDeptName(String deptName);

    /**
     * 通过 部门id列表获取部门名称列表
     *
     * @return List<String>
     */
    List<String> getDeptNamesByIds(@Param("ids") List<Long> ids);

    /**
     * 获取部门列表
     *
     * @param searchForm 查询对象
     * @return List<Department>
     */
    List<Department> getList(DepartmentSearchForm searchForm);

    /**
     * 通过部门 id 获取部门总人数
     *
     * @param id id
     * @return Integer
     */
    Integer getDeptPersonsById(Long id);
}
