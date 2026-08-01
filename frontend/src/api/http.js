import axios from 'axios'

// 统一 axios 实例（BETA 4.5：所有 HTTP 集中在 frontend/src/api/，.vue 禁止直接使用 axios）。
// baseURL 用相对路径，沿用现有 Login/Register 的调用约定。
const http = axios.create({
  baseURL: '/'
})

export default http
