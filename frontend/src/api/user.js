import http from './http'

// 用户域接口（沿用既有 /api/users 契约）
export function login(payload) {
  return http.post('/api/users/login', payload).then(res => res.data)
}

export function register(payload) {
  return http.post('/api/users/register', payload).then(res => res.data)
}
