import request from '@/utils/request'

export const getDruidLoginConfig = () => {
  return request({
    url: '/system/common/druid',
    method: 'GET'
  })
}

export const getSystemAuthorInfo = () => {
  return request({
    url: '/system/common/sysinfo',
    method: 'GET'
  })
}
