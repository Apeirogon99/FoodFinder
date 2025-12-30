import api from './index'

/**
 * AI 추천 관련 API
 */
export const recommendApi = {
  /**
   * AI 음식점 추천 요청
   * @param {Object} data - 추천 요청 데이터
   * @param {number} data.latitude - 위도
   * @param {number} data.longitude - 경도
   * @param {number} data.radius - 반경 (100~500m)
   * @param {string[]} data.hashTagCodes - 해시태그 코드 배열
   * @param {number[]} [data.excludeRestaurantIds] - 제외할 음식점 ID 배열
   * @returns {Promise<RestaurantDetail>}
   */
  async getRecommendation(data) {
    const response = await api.post('/v1/ai/recommend', data)
    return response.data
  },
}

export default recommendApi
