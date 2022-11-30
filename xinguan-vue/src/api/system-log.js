import request from '@/utils/request'

export const getLogList = (page, size, searchForm) => {
  return request({
    url: '/system/operator/log/list',
    method: 'GET',
    params: {
      page, size, ...searchForm
    }
  })
}

export const delLog = (id) => {
  return request({
    url: `/system/operator/log/${id}`,
    method: 'DELETE'
  })
}

export const batchDelLog = (ids) => {
  return request({
    url: '/system/operator/log/batch',
    method: 'DELETE',
    data: ids
  })
}
