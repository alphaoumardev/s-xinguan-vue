import request from '@/utils/request'

export const getRoleList = (searchForm) => {
  return request({
    url: '/system/role/list',
    method: 'POST',
    data: searchForm
  })
}

// 获取所有的角色列表
export const getAllRoles = () => {
  return request({
    url: '/system/role/all',
    method: 'GET'
  })
}

// 根据角色id获取菜单 ids
export const getMenuListByRoleId = (id) => {
  return request({
    url: `/system/role/roleMenu/${id}`,
    method: 'GET'
  })
}

// 根据角色id设置该角色所拥有的菜单权限
export const assignMenus = (id, mids) => {
  return request({
    url: `/system/role/${id}/menus`,
    method: 'POST',
    data: {
      mids: mids
    }
  })
}

export const updateRoleStatus = (id, forbidden) => {
  return request({
    url: '/system/role/forbidden',
    method: 'PUT',
    params: {
      id, forbidden
    }
  })
}

export const addRole = (roleForm) => {
  return request({
    url: '/system/role/add',
    method: 'POST',
    data: roleForm
  })
}

export const delRole = (id) => {
  return request({
    url: `/system/role/delete/${id}`,
    method: 'DELETE'
  })
}

export const updateRole = (roleForm) => {
  return request({
    url: '/system/role/update/',
    method: 'PUT',
    data: roleForm
  })
}

export const exportRole = (searchForm) => {
  return request({
    url: '/system/role/export/',
    method: 'POST',
    data: searchForm,
    responseType: 'arraybuffer'
  })
}
