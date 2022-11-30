import request from '@/utils/request'

export const getUsersPage = (page, size) => {
  return request({
    url: '/system/user/list',
    method: 'GET',
    params: {
      page, size
    }
  })
}

export const searchUserListPage = (searchForm, page, size) => {
  return request({
    url: '/system/user/search',
    method: 'POST',
    params: {
      page, size
    },
    data: searchForm
  })
}

export const updateUserStatus = (id, forbidden) => {
  return request({
    url: '/system/user/forbidden',
    method: 'PUT',
    params: {
      id, forbidden
    }
  })
}

export const login = (loginForm) => {
  return request({
    url: '/system/user/login',
    method: 'POST',
    data: loginForm
  })
}

// 获取验证码
export const verifyCode = (historyId) => {
  return request({
    url: `/system/user/captchaImage/${historyId}`,
    method: 'GET'
  })
}
export const resetPwd = (id) => {
  return request({
    url: '/system/user/pwd/' + id,
    method: 'PUT'
  })
}

export const addUser = (userForm) => {
  return request({
    url: '/system/user/add',
    method: 'POST',
    data: userForm
  })
}

export const editUser = (userForm, id) => {
  return request({
    url: '/system/user/edit/' + id,
    method: 'PUT',
    data: userForm
  })
}

export const deleteUser = (id) => {
  return request({
    url: `/system/user/delete/${id}`,
    method: 'DELETE'
  })
}

export const getUserAvatar = (username) => {
  return request({
    url: '/system/user/avatar',
    method: 'GET',
    params: {
      username
    }
  })
}

export const getUserById = (id) => {
  return request({
    url: '/system/user/' + id,
    method: 'GET'
  })
}

// 修改密码
export const resetSelfPwd = (resetForm) => {
  return request({
    url: '/system/user/self/pwd',
    method: 'PUT',
    data: resetForm
  })
}

// 获取用户所拥有的角色IDS
export const getRoleIdsByUserId = (id) => {
  return request({
    url: `/system/user/${id}/roles`,
    method: 'GET'
  })
}

// 为用户分配角色
export const assignRoles = (id, rids) => {
  return request({
    url: `/system/user/${id}/roles`,
    method: 'POST',
    data: {
      rids: rids
    }
  })
}

// 导出用户列表
export const exportList = (searchForm) => {
  return request({
    url: '/system/user/export',
    method: 'POST',
    data: searchForm,
    responseType: 'arraybuffer'
  })
}
