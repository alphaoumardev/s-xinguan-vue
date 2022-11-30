import request from '@/utils/request'

export const getInStockList = (page, size, form) => {
  return request({
    url: '/business/in-stock/selectList',
    method: 'GET',
    params: {
      page, size, ...form
    }
  })
}

// 导出入库列表
export const exportInStock = (searchForm) => {
  return request({
    url: '/business/in-stock/export',
    method: 'GET',
    params: {
      ...searchForm
    },
    responseType: 'arraybuffer'
  })
}

// 获取入库记录明细
export const getInStockDetailList = (id, page, size) => {
  return request({
    url: `/business/in-stock/detail/${id}`,
    method: 'GET',
    params: {
      page, size
    }
  })
}

// 移入回收站
export const remove = (id) => {
  return request({
    url: `/business/in-stock/remove/${id}`,
    method: 'PUT'
  })
}

// 移出回收站
export const backRecord = (id) => {
  return request({
    url: `/business/in-stock/back/${id}`,
    method: 'PUT'
  })
}

// 入库审核
export const publishRecord = (id) => {
  return request({
    url: `/business/in-stock/publish/${id}`,
    method: 'PUT'
  })
}

// 删除物资入库记录
export const delInStock = (id) => {
  return request({
    url: `/business/in-stock/delete/${id}`,
    method: 'DELETE'
  })
}

// 添加物资入库记录
export const addInStock = (data) => {
  return request({
    url: '/business/in-stock/addInStock/',
    method: 'POST',
    data: data
  })
}
