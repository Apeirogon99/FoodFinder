import api from './index'

export const statisticsApi = {
  // 카테고리별 통계
  getMyCategoryStats(params) {
    return api.get('/v1/stats/user/category', { params })
  },
  
  // 추천 반응 통계
  getMyReactionStats(params) {
    return api.get('/v1/stats/user/reaction', { params })
  },
  
  // 최근 활동 내역
  getMyRecentStats() {
    return api.get('/v1/stats/user/recent')
  }
}
