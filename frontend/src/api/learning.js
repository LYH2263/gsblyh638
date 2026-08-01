import request from './request'

export function getRecommendations(userId) {
  return request.get('/api/learning/paths/recommendations', {
    params: { userId },
  })
}

export function reportBehavior({ userId, pathId, studyDuration, masteryScore }) {
  return request.post('/api/learning/behaviors', {
    userId,
    pathId,
    studyDuration,
    masteryScore,
  })
}

export function getPathProgress(userId, pathId) {
  return request.get(`/api/learning/paths/${pathId}/progress`, {
    params: { userId },
  })
}

export function getAnalyticsSummary(userId) {
  return request.get('/api/learning/analytics/summary', {
    params: { userId },
  })
}
