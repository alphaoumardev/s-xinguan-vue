import request from '@/utils/request'

export const getDeptNameAndCount = () => {
  return request({
    url: '/system/department/selectList',
    method: 'GET'
  })
}

export const getIdByDeptName = (deptName) => {
  return request({
    url: '/system/department',
    method: 'GET',
    params: {
      deptName
    }
  })
}

export const getList = (searchForm) => {
  return request({
    url: '/system/department/list',
    method: 'POST',
    data: searchForm
  })
}

export const addDepartment = (deptForm) => {
  return request({
    url: '/system/department/add',
    method: 'POST',
    data: deptForm
  })
}

export const updateDepartment = (daptForm) => {
  return request({
    url: '/system/department/update',
    method: 'PUT',
    data: daptForm
  })
}

export const delDept = (id) => {
  return request({
    url: `/system/department/del/${id}`,
    method: 'DELETE'
  })
}

// 导出用户列表
export const exportDept = (searchForm) => {
  return request({
    url: '/system/department/export',
    method: 'POST',
    data: searchForm,
    responseType: 'arraybuffer'
  })
}
