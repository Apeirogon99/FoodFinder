import api from './index'

/**
 * 리뷰 관련 API
 */
export const reviewApi = {
  /**
   * 리뷰 작성 (추천 ID 기반)
   * @param {number} recommendId - 추천 ID
   * @param {Object} data - {content: string, rating: number}
   */
  async createReview(recommendId, data) {
    await api.post(`/v1/reviews/${recommendId}`, data)
  },

  /**
   * 리뷰 수정
   * @param {number} reviewId - 리뷰 ID
   * @param {Object} data - {content: string, rating: number}
   */
  async updateReview(reviewId, data) {
    await api.put(`/v1/reviews/${reviewId}`, data)
  },

  /**
   * 리뷰 삭제
   * @param {number} reviewId - 리뷰 ID
   */
  async deleteReview(reviewId) {
    await api.delete(`/v1/reviews/${reviewId}`)
  },

  /**
   * 내 리뷰 목록 조회 (커서 페이지네이션)
   * @param {number|null} cursorId - 커서 ID (다음 페이지 요청 시)
   * @returns {Promise<{reviews: Review[], nextCursor: number|null, hasNext: boolean}>}
   */
  async getMyReviews(cursorId = null) {
    const params = cursorId ? { cursorId } : {}
    const response = await api.get(`/v1/reviews/me`, { params })
    return response.data
  },

  /**
   * 음식점 리뷰 목록 조회 (커서 페이지네이션)
   * @param {number} restaurantId - 음식점 ID
   * @param {number|null} cursorId - 커서 ID (다음 페이지 요청 시)
   * @returns {Promise<{reviews: Review[], averageRating: number, nextCursor: number|null, hasNext: boolean}>}
   */
  async getRestaurantReviews(restaurantId, cursorId = null) {
    const params = cursorId ? { cursorId } : {}
    const response = await api.get(`/v1/restaurants/review/${restaurantId}`, { params })
    return response.data
  },
}

export default reviewApi
