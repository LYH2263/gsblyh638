import http from './http'

export function login(username, password) {
  return http.post('/api/users/login', { username, password })
}

export function register(payload) {
  return http.post('/api/users/register', payload)
}
