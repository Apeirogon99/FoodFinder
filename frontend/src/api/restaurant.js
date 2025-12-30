import api from './index'

/**
 * 음식점 관련 API
 */
export const restaurantApi = {
  /**
   * 주변 음식점 검색
   * @param {Object} data - 검색 조건
   * @param {number} data.latitude - 위도
   * @param {number} data.longitude - 경도
   * @param {number} data.radius - 반경 (100~500m)
   * @returns {Promise<{restaurants: Restaurant[]}>}
   */
  async searchNearby(data) {
    const response = await api.post('/v1/restaurants/search', data)
    return response.data
  },
}

export default restaurantApi
