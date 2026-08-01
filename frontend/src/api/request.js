import axios from 'axios'

const request = axios.create({
  baseURL: '/',
  timeout: 10000,
})

request.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && typeof body === 'object' && 'code' in body) {
      if (body.code === 0) {
        return body.data
      }
      return Promise.reject(new Error(body.message || '请求失败'))
    }
    return body
  },
  (error) => {
    const body = error.response?.data
    if (body && typeof body === 'object' && 'message' in body) {
      return Promise.reject(new Error(body.message))
    }
    return Promise.reject(new Error(error.message || '网络错误'))
  }
)

export default request
