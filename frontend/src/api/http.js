import axios from 'axios'

const http = axios.create({
  baseURL: '/',
  timeout: 15000,
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const envelope = error.response && error.response.data
    if (envelope && typeof envelope === 'object' && 'message' in envelope) {
      return Promise.reject(new Error(envelope.message || '请求失败'))
    }
    return Promise.reject(error)
  }
)

export default http
