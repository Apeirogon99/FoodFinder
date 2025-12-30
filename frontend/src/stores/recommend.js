import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useRecommendStore = defineStore('recommend', () => {
  // ========== State ==========
  
  // 추천 컨텍스트 (위치, 반경, 해시태그)
  const context = ref(null)
  
  // 제외할 식당 ID 목록
  const excludedRestaurantIds = ref([])
  
  // 현재 추천된 식당
  const currentRestaurant = ref(null)

  // ========== Getters ==========
  
  /**
   * 컨텍스트가 설정되어 있는지 확인
   */
  const hasContext = computed(() => context.value !== null)
  
  /**
   * API 요청 객체 생성
   */
  const getRecommendRequest = computed(() => {
    if (!context.value) return null
    
    return {
      latitude: context.value.latitude,
      longitude: context.value.longitude,
      radius: context.value.radius,
      hashTagCodes: context.value.hashTagCodes,
      excludeRestaurantIds: excludedRestaurantIds.value.length > 0 
        ? excludedRestaurantIds.value 
        : undefined
    }
  })

  // ========== Actions ==========
  
  /**
   * 추천 컨텍스트 설정
   * @param {Object} newContext - 추천 컨텍스트
   * @param {number} newContext.latitude - 위도
   * @param {number} newContext.longitude - 경도
   * @param {number} newContext.radius - 반경
   * @param {string[]} newContext.hashTagCodes - 해시태그 코드 배열
   */
  function setContext(newContext) {
    context.value = {
      latitude: newContext.latitude,
      longitude: newContext.longitude,
      radius: newContext.radius,
      hashTagCodes: [...newContext.hashTagCodes]
    }
  }
  
  /**
   * 제외 목록에 식당 ID 추가
   * @param {number|string} restaurantId - 식당 ID
   */
  function addExcludedRestaurant(restaurantId) {
    if (restaurantId && !excludedRestaurantIds.value.includes(restaurantId)) {
      excludedRestaurantIds.value.push(restaurantId)
    }
  }
  
  /**
   * 현재 식당 설정
   * @param {Object} restaurant - 식당 정보
   */
  function setCurrentRestaurant(restaurant) {
    currentRestaurant.value = restaurant
  }
  
  /**
   * 모든 상태 초기화
   */
  function clearAll() {
    context.value = null
    excludedRestaurantIds.value = []
    currentRestaurant.value = null
  }

  return {
    // State
    context,
    excludedRestaurantIds,
    currentRestaurant,
    
    // Getters
    hasContext,
    getRecommendRequest,
    
    // Actions
    setContext,
    addExcludedRestaurant,
    setCurrentRestaurant,
    clearAll,
  }
})
