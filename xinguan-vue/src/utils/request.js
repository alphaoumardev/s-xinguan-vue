import axios from 'axios'
import { Message, MessageBox } from 'element-ui'
import router from '@/router'

const instance = axios.create({
  baseURL: process.env.VUE_APP_BASE_API,
  timeout: 6000
})

// 配置请求拦截器，在每次请求都设置 token
instance.interceptors.request.use((config) => {
  const token = window.sessionStorage.getItem('token')
  if (token)
  {
    config.headers.Authorization = token
  }
  return config
}, () => {})

let loginPage = false

// 配置响应拦截器
instance.interceptors.response.use((response) => {
  // 处理导出问题
  if (response.data instanceof ArrayBuffer) {
    loginPage = true
    return response.data
  } else {
    if (response.data.code !== 200) {
      // 不等于 200 有可能是导出文件
      switch (response.data.code) {
        case 2001:
        case 7009:
        case 2007:
          loginPage = true
          Message.error(response.data.message)
          window.sessionStorage.clear()
          break
        case 9002:
          if (!loginPage) {
            MessageBox.confirm(response.data.message, '系统提示', {
              type: 'warning'
            }).then(() => {
              router.replace({
                path: '/login'
              }).then(ignore => {})
            })
            window.sessionStorage.clear()
            loginPage = true
          }
          break
        default:
          Message.error(response.data.message)
      }
      return Promise.reject(response.data.message)
    } else {
      return response.data
    }
  }
}, (error) => {
  // Network Error
  // Request failed with status code 500
  // timeout of 6000ms exceeded
  if (error.message === 'Network Error') {
    Message.error('网络连接失败，请检查网络是否正常工作！')
  } else if (error.message === 'Request failed with status code 500') {
    Message.error('服务器正在开小差，请稍后再试！')
  } else if (error.message === 'timeout of 6000ms exceeded') {
    Message.error('网络请求超时！')
  } else {
    Message.error(error.message)
  }
  return Promise.reject(error)
})

export default instance
