import axios from 'axios'

const api = axios.create({
    baseURL: '/api/v1',
    timeout: 10000,
    withCredentials: true
})

export const restaurantApi = {

    /**
     * 주변 음식점 검색
     * @param {number} latitude - 위도
     * @param {number} longitude - 경도
     * @param {number} radius - 검색 범위 (미터)
     */
    searchNearbyRestaurants(latitude, longitude, radius = 500) {
        return api.post('/restaurants/search', {
            latitude,
            longitude,
            radius
        })
    },

    /**
     * AI 음식점 추천 요청
     * @param {number} latitude - 위도
     * @param {number} longitude - 경도
     * @param {number} radius - 검색 범위 (미터)
     */
    getRecommendation(latitude, longitude, radius) {
        return api.post('/restaurants/recommend', {
            latitude,
            longitude,
            radius
        })
    },

    /**
     * 다른 음식점 추천 요청 (이전 추천 제외)
     * @param {number} latitude - 위도
     * @param {number} longitude - 경도
     * @param {number} radius - 검색 범위 (미터)
     * @param {string[]} excludeIds - 제외할 음식점 ID 목록
     */
    getAnotherRecommendation(latitude, longitude, radius, excludeIds = []) {
        return api.post('/restaurants/recommend/another', {
            latitude,
            longitude,
            radius,
            excludeIds
        })
    },

    /**
     * 음식점 상세 정보 조회
     * @param {string} restaurantId - 음식점 ID
     */
    getRestaurantDetail(restaurantId) {
        return api.get(`/restaurants/${restaurantId}`)
    }
}

export default restaurantApi
