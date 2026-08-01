import axios from 'axios'

// 统一 axios 实例：前端所有 HTTP 必须经由 src/api/ 封装（规格书第四章 BETA）
const http = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

export default http
