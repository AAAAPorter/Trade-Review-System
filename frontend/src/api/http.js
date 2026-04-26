import axios from 'axios'
import { message } from 'antd'

const http = axios.create({
  baseURL: '/api',
  timeout: 10000
})

http.interceptors.response.use(
  (response) => response.data,
  (error) => {
    message.error(error.response?.data?.message || '请求失败，请稍后重试')
    return Promise.reject(error)
  }
)

export default http
