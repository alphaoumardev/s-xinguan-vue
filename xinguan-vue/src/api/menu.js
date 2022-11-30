import request from '@/utils/request'

export const getMenuAndButton = () => {
  return request({
    url: '/system/menu/list',
    method: 'GET'
  })
}

export const getMenus = () => {
  return request({
    url: '/system/menu/menus',
    method: 'GET'
  })
}

export const updateMenu = (formData) => {
  return request({
    url: '/system/menu',
    method: 'PUT',
    data: formData
  })
}

export const submitMenu = (formData) => {
  return request({
    url: '/system/menu',
    method: 'POST',
    data: formData
  })
}

export const deleteMenu = (id) => {
  return request({
    url: `/system/menu/${id}`,
    method: 'DELETE'
  })
}

export const exportMenu = (searchForm) => {
  return request({
    url: '/system/menu/export',
    method: 'POST',
    data: searchForm,
    responseType: 'arraybuffer'
  })
}
