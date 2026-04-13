import axios from 'axios'
import { showToast } from './toast'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.request.use(
  config => {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (user.token) {
      config.headers.Authorization = `Bearer ${user.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    console.error('API 请求出错:', error)
    
    if (error.response) {
      switch (error.response.status) {
        case 401:
          localStorage.removeItem('user')
          window.location.href = '/login'
          break
        case 403:
          showToast('没有权限访问该资源', 'error')
          break
        case 404:
          showToast('请求的资源不存在', 'error')
          break
        case 500:
          showToast('服务器内部错误', 'error')
          break
        default:
          showToast(`请求失败: ${error.response.data.message || '未知错误'}`, 'error')
      }
    } else if (error.request) {
      console.warn('网络连接失败，请检查网络设置')
    } else {
      console.warn('请求配置错误: ' + error.message)
    }
    
    return Promise.reject(error)
  }
)

api.withRetry = (config, maxRetries = 3) => {
  let retries = 0
  
  const attempt = () => {
    return api(config).catch(error => {
      retries++
      if (retries <= maxRetries && error.code === 'ECONNABORTED') {
        console.log(`请求重试 ${retries}/${maxRetries}...`)
        return attempt()
      }
      return Promise.reject(error)
    })
  }
  
  return attempt()
}

export default api
