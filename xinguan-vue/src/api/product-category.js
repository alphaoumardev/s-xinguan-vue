import request from '@/utils/request'

// 分类列表
export const findProductList = (page, size, form) => {
  return request({
    url: '/business/product-category/findProductCategoryList',
    method: 'GET',
    params: {
      page, size, ...form
    }
  })
}

// 分类树形结构
export const categoryTree = (page, size) => {
  return request({
    url: '/business/product-category/categoryTree',
    method: 'GET',
    params: {
      page, size
    }
  })
}

// 父级分类树
export const getParentCategoryTree = () => {
  return request({
    url: '/business/product-category/getParentCategoryTree',
    method: 'GET'
  })
}

// 所有分类
export const findAll = () => {
  return request({
    url: '/business/product-category/findAll',
    method: 'GET'
  })
}

// 添加分类
export const add = (data) => {
  return request({
    url: '/business/product-category/add',
    method: 'POST',
    data: data
  })
}

// 根据ID获取分类信息
export const findById = (id) => {
  return request({
    url: `/business/product-category/findById/${id}`,
    method: 'GET'
  })
}

// 更新分类
export const update = (id, data) => {
  return request({
    url: `/business/product-category/update/${id}`,
    method: 'PUT',
    data: data
  })
}

// 删除分类
export const delProCat = (id) => {
  return request({
    url: `/business/product-category/delete/${id}`,
    method: 'DELETE'
  })
}
