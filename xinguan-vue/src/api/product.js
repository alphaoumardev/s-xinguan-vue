import request from '@/utils/request'

export const findProductList = (page, size, categorys, filter, form) => {
  return request({
    url: '/business/product/findProductList',
    method: 'GET',
    params: {
      page, size, categorys, filter, ...form
    }
  })
}

// 库存列表
export const findProductStocks = (page, size, categorys, form) => {
  return request({
    url: '/business/product/findProductStocks',
    method: 'GET',
    params: {
      page, size, categorys, ...form
    }
  })
}

// 获取所有库存列表
export const findAllProductStocks = (categorys, form) => {
  return request({
    url: '/business/product/findAllProductStocks',
    method: 'GET',
    params: {
      categorys, ...form
    }
  })
}

// 添加物资
export const add = (form) => {
  return request({
    url: '/business/product/add',
    method: 'POST',
    data: form
  })
}

// 根据ID获取物资资料
export const findById = (id) => {
  return request({
    url: `/business/product/findById/${id}`,
    method: 'GET'
  })
}

// 更新物资
export const update = (id, data) => {
  return request({
    url: `/business/product/update/${id}`,
    method: 'POST',
    data: data
  })
}

// 删除物资
export const delProduct = (id) => {
  return request({
    url: `/business/product/delete/${id}`,
    method: 'DELETE'
  })
}

// 移入回收站
export const remove = (id) => {
  return request({
    url: `/business/product/remove/${id}`,
    method: 'PUT'
  })
}

// 恢复物资
export const backRecord = (id) => {
  return request({
    url: `/business/product/back/${id}`,
    method: 'PUT'
  })
}

// 物资添加审核
export const publishRecord = (id) => {
  return request({
    url: `/business/product/publish/${id}`,
    method: 'PUT'
  })
}
