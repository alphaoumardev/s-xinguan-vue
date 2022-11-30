import request from '@/utils/request'

// 所有供应商列表
export const findAll = () => {
  return request({
    url: '/business/supplier/findAll',
    method: 'GET'
  })
}

// 分页供应商列表
export const findSupplierList = (page, size, form) => {
  return request({
    url: '/business/supplier/findSupplierList',
    method: 'GET',
    params: {
      page, size, ...form
    }
  })
}

// 添加
export const add = (data) => {
  return request({
    url: '/business/supplier/add',
    method: 'POST',
    data: data
  })
}

// 根据ID获取
export const findById = (id) => {
  return request({
    url: `/business/supplier/findById/${id}`,
    method: 'GET'
  })
}

// 根据ID获取
export const update = (id, data) => {
  return request({
    url: `/business/supplier/update/${id}`,
    method: 'PUT',
    data: data
  })
}

// 删除
export const delSupplier = (id) => {
  return request({
    url: `/business/supplier/delete/${id}`,
    method: 'DELETE'
  })
}
