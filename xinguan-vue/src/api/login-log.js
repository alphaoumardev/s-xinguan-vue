import request from '@/utils/request'

export const getLoginLogList = (searchForm, page, size) => {
  return request({
    url: '/system/login-log/list',
    method: 'POST',
    params: {
      page, size
    },
    data: searchForm
  })
}

export const delLoginLog = (id) => {
  return request({
    url: `/system/login-log/${id}`,
    method: 'DELETE'
  })
}

export const batchDelLoginLog = (ids) => {
  return request({
    url: `/system/login-log/batch/${ids}`,
    method: 'DELETE'
  })
}

export const loginReport = (username) => {
  return request({
    url: `/system/login-log/loginReport/${username}`,
    method: 'GET'
  })
}
