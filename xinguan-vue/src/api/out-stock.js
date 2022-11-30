import request from '@/utils/request'

// 发放物资
export const addOutStock = (data) => {
  return request({
    url: '/business/out-stock/addOutStock',
    method: 'POST',
    data: data
  })
}

// 出库单列表
export const findOutStockList = (page, size, form) => {
  return request({
    url: '/business/out-stock/findOutStockList',
    method: 'GET',
    params: {
      page, size, ...form
    }
  })
}

// 移入回收站
export const remove = (id) => {
  return request({
    url: `/business/out-stock/remove/${id}`,
    method: 'PUT'
  })
}

// 获取发放单明细
export const getOutStockDetailList = (id, page, size) => {
  return request({
    url: `/business/out-stock/detail/${id}`,
    method: 'GET',
    params: {
      page, size
    }
  })
}

// 删除物资发放记录
export const delOutStock = (id) => {
  return request({
    url: `/business/out-stock/delete/${id}`,
    method: 'DELETE'
  })
}

// 入库审核
export const publishRecord = (id) => {
  return request({
    url: `/business/out-stock/publish/${id}`,
    method: 'PUT'
  })
}

// 移出回收站
export const backRecord = (id) => {
  return request({
    url: `/business/out-stock/back/${id}`,
    method: 'PUT'
  })
}
