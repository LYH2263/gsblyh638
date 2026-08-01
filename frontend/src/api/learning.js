import http from './http'

export function getRecommendations(userId) {
  return http.get('/api/learning/paths/recommendations', {
    params: { userId },
  })
}

export function reportBehavior({ userId, pathId, studyDuration, masteryScore }) {
  return http.post('/api/learning/behaviors', {
    userId,
    pathId,
    studyDuration,
    masteryScore,
  })
}

export function getPathProgress(userId, pathId) {
  return http.get(`/api/learning/paths/${pathId}/progress`, {
    params: { userId },
  })
}

export function getAnalyticsSummary(userId) {
  return http.get('/api/learning/analytics/summary', {
    params: { userId },
  })
}
