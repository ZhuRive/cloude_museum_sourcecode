import axios from 'axios'
import { showToast, showDialog } from 'vant'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('userToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 401) {
      localStorage.removeItem('userToken')
      localStorage.removeItem('userInfo')
      showDialog({
        title: '提示',
        message: '登录已过期，请重新登录',
        confirmButtonText: '确定'
      }).then(() => {
        window.location.href = '/login'
      })
      return Promise.reject(new Error('登录已过期'))
    }
    if (res.code !== 200) {
      showToast(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    showToast(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
