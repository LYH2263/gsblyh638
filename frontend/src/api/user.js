import http from './http'

// 用户域接口（BETA 4.5：HTTP 收敛到 api 层）。
export function login(payload) {
  return http.post('/api/users/login', payload)
}

export function register(payload) {
  return http.post('/api/users/register', payload)
}
