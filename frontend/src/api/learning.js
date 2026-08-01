import http from './http'

// 学习域接口封装（BETA 4.1 前缀 /api/learning；BETA 4.5 HTTP 收敛到 api 层）。
// 后端统一包络 { code, message, data }，各方法直接返回该包络体。

// ★ 推荐列表（GAMMA 8.2：仅 active、过滤已完成、mastery 升序）
export function getRecommendations(userId) {
  return http.get('/api/learning/paths/recommendations', { params: { userId } })
}

// ★ 上报学习行为（GAMMA 8.1/8.3：studyDuration 分钟正整数、mastery 0-100）
export function reportBehavior(payload) {
  return http.post('/api/learning/behaviors', payload)
}

// ★ 单路径进度
export function getPathProgress(pathId, userId) {
  return http.get(`/api/learning/paths/${pathId}/progress`, { params: { userId } })
}

// ☆ 分析汇总（GAMMA 8.4）
export function getAnalyticsSummary(userId) {
  return http.get('/api/learning/analytics/summary', { params: { userId } })
}

// ☆ 最近学习动态
export function getRecentBehaviors(userId, limit = 10) {
  return http.get('/api/learning/behaviors/recent', { params: { userId, limit } })
}

// ☆ 路径主数据
export function getPaths() {
  return http.get('/api/learning/paths')
}
