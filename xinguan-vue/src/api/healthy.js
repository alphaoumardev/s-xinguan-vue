import request from '@/utils/request'

export const getYqData = () => {
  return request({
    url: '/health/yq/data',
    method: 'GET'
  })
}

export const isReport = () => {
  return request({
    url: '/health/isReport',
    method: 'GET'
  })
}

export const report = (info) => {
  return request({
    url: '/health/report',
    method: 'POST',
    data: info
  })
}

export const history = (page, size) => {
  return request({
    url: '/health/history',
    method: 'GET',
    params: { page, size }
  })
}

export const points = (page, size, form) => {
  return request({
    url: '/health/points',
    method: 'GET',
    params: { page, size, ...form }
  })
}

export const delPoint = (id) => {
  return request({
    url: `/health/delete/${id}`,
    method: 'DELETE'
  })
}

// 导出打卡记录列表
export const exportPoints = (searchForm) => {
  return request({
    url: '/health/export',
    method: 'GET',
    params: {
      ...searchForm
    },
    responseType: 'arraybuffer'
  })
}
