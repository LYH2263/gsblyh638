import http from './http'

// 学习域接口（/api/learning，响应统一为 code/message/data 包络）

// ★ 推荐列表：薄弱优先（masteryScore 升序），已完成路径不出现在结果中
export function getRecommendations(userId) {
  return http.get('/api/learning/paths/recommendations', { params: { userId } }).then(res => res.data)
}

// ★ 上报学习行为：studyDuration 为分钟正整数，masteryScore 为 0-100 整数
export function reportBehavior(payload) {
  return http.post('/api/learning/behaviors', payload).then(res => res.data)
}

// ★ 单路径进度
export function getPathProgress(pathId, userId) {
  return http.get(`/api/learning/paths/${pathId}/progress`, { params: { userId } }).then(res => res.data)
}

// ☆ 分析汇总：totalStudyMinutes/completedPathCount/inProgressPathCount/weakestPaths
export function getAnalyticsSummary(userId) {
  return http.get('/api/learning/analytics/summary', { params: { userId } }).then(res => res.data)
}
