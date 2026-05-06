import axios from 'axios'
import { message } from 'antd'

// 统一的 Axios 实例。Vite 开发环境会把 /api 代理到后端服务。
const http = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 后端正常响应时只把 data 交给页面；异常时统一弹出 message，并继续抛给页面做局部处理。
http.interceptors.response.use(
  (response) => response.data,
  (error) => {
    message.error(error.response?.data?.message || '请求失败，请稍后重试')
    return Promise.reject(error)
  }
)

export default http
