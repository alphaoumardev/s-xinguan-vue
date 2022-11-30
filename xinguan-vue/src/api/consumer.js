import request from '@/utils/request'

// 去向列表
export const findConsumerList = (page, size, data) => {
  return request({
    url: '/business/consumer/findConsumerList',
    method: 'GET',
    params: {
      page, size, ...data
    }
  })
}

// 添加去向
export const add = (data) => {
  return request({
    url: '/business/consumer/add',
    method: 'POST',
    data: data
  })
}

// 根据id获取一条数据
export const findById = (id) => {
  return request({
    url: `/business/consumer/findById/${id}`,
    method: 'GET'
  })
}

// 更新去向
export const update = (id, data) => {
  return request({
    url: `/business/consumer/update/${id}`,
    method: 'PUT',
    data: data
  })
}

// 删除去向
export const deleteConsumer = (id) => {
  return request({
    url: `/business/consumer/delete/${id}`,
    method: 'DELETE'
  })
}

// 所有去向
export const findAll = () => {
  return request({
    url: '/business/consumer/findAll/',
    method: 'GET'
  })
}
