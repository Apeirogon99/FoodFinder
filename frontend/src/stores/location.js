import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useLocationStore = defineStore('location', () => {
  // ========== State ==========
  const latitude = ref(null)
  const longitude = ref(null)
  const isLoading = ref(false)
  const error = ref(null)
  const radius = ref(300) // 기본 반경 300m

  // ========== Getters ==========
  const hasLocation = computed(() => latitude.value !== null && longitude.value !== null)
  
  const currentLocation = computed(() => {
    if (!hasLocation.value) return null
    return {
      latitude: latitude.value,
      longitude: longitude.value,
    }
  })

  // ========== Actions ==========
  
  /**
   * 현재 위치 가져오기
   */
  function getCurrentLocation() {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        const err = new Error('이 브라우저에서는 위치 정보를 지원하지 않습니다.')
        error.value = err.message
        reject(err)
        return
      }

      isLoading.value = true
      error.value = null

      navigator.geolocation.getCurrentPosition(
        (position) => {
          latitude.value = position.coords.latitude
          longitude.value = position.coords.longitude
          isLoading.value = false
          resolve({
            latitude: latitude.value,
            longitude: longitude.value,
          })
        },
        (err) => {
          isLoading.value = false
          let message = '위치 정보를 가져올 수 없습니다.'
          
          switch (err.code) {
            case err.PERMISSION_DENIED:
              message = '위치 정보 접근이 거부되었습니다. 브라우저 설정에서 위치 권한을 허용해주세요.'
              break
            case err.POSITION_UNAVAILABLE:
              message = '위치 정보를 사용할 수 없습니다.'
              break
            case err.TIMEOUT:
              message = '위치 정보 요청 시간이 초과되었습니다.'
              break
          }
          
          error.value = message
          reject(new Error(message))
        },
        {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 300000, // 5분 캐시
        }
      )
    })
  }

  /**
   * 위치 수동 설정
   */
  function setLocation(lat, lng) {
    latitude.value = lat
    longitude.value = lng
    error.value = null
  }

  /**
   * 반경 설정
   */
  function setRadius(value) {
    // 100 ~ 500 범위로 제한
    radius.value = Math.min(500, Math.max(100, value))
  }

  /**
   * 위치 초기화
   */
  function clearLocation() {
    latitude.value = null
    longitude.value = null
    error.value = null
  }

  return {
    // State
    latitude,
    longitude,
    isLoading,
    error,
    radius,
    
    // Getters
    hasLocation,
    currentLocation,
    
    // Actions
    getCurrentLocation,
    setLocation,
    setRadius,
    clearLocation,
  }
})
