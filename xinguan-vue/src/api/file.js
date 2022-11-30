import request from '@/utils/request'

export const uploadFile = (file) => {
  return request({
    url: '/system/oss/upload',
    method: 'POST',
    data: file
  })
}

export const deleteFile = (fileName) => {
  return request({
    url: '/system/oss/delete',
    method: 'DELETE',
    params: {
      fileName
    }
  })
}

export const listFile = (page, size, searchForm) => {
  return request({
    url: '/system/oss/list',
    method: 'POST',
    params: {
      page, size
    },
    data: searchForm
  })
}
