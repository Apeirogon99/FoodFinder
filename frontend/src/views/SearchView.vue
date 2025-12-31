<template>
  <AppLayout title="위치 선택" :show-back="true">
    <!-- 선택된 태그 표시 -->
    <div v-if="selectedTags.length > 0" class="selected-tags-bar">
      <span class="tags-label">선택된 태그:</span>
      <div class="tags-list">
        <el-tag 
          v-for="tag in selectedTagLabels" 
          :key="tag" 
          size="small"
          effect="dark"
        >
          {{ tag }}
        </el-tag>
      </div>
    </div>

    <!-- 메인 컨텐츠: 카카오맵 영역 -->
    <div class="map-section">
      <!-- 맵 로딩 중 -->
      <div v-if="isMapLoading" class="map-loading">
        <el-icon class="loading-icon" :size="32">
          <Loading />
        </el-icon>
        <span>지도를 불러오는 중...</span>
      </div>
      
      <!-- 맵 에러 -->
      <div v-else-if="mapError" class="map-error">
        <span class="error-icon">⚠️</span>
        <span>{{ mapError }}</span>
        <el-button size="small" @click="retryLoadMap">다시 시도</el-button>
      </div>
      
      <!-- 카카오맵 컨테이너 -->
      <div ref="mapContainer" class="map-container" :class="{ hidden: isMapLoading || mapError }"></div>
      
      <!-- 현재 위치 버튼 -->
      <button 
        v-if="!isMapLoading && !mapError"
        class="current-location-btn" 
        @click="moveToCurrentLocation"
      >
        <span class="btn-icon">📍</span>
        현재 위치
      </button>
    </div>

    <!-- 하단 컨트롤 영역 -->
    <template #footer>
      <div class="control-section">
        <!-- 현재 범위 표시 -->
        <div class="radius-display">
          <span class="radius-value">{{ radius }}m</span>
          <span class="radius-label">검색 반경</span>
        </div>

        <!-- 범위 슬라이더 -->
        <div class="slider-container">
          <span class="slider-label">100m</span>
          <el-slider
            v-model="radius"
            :min="100"
            :max="500"
            :step="50"
            :show-tooltip="false"
            @input="updateCircle"
          />
          <span class="slider-label">500m</span>
        </div>

        <!-- 추천 받기 버튼 -->
        <el-button
          type="primary"
          class="recommend-btn"
          :loading="isLoading"
          :disabled="!hasLocation"
          @click="requestRecommendation"
        >
          <template v-if="!hasLocation">
            위치를 확인해주세요
          </template>
          <template v-else>
            🤖 AI 추천 받기
          </template>
        </el-button>
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Loading } from '@element-plus/icons-vue'
import { loadKakaoMap } from '@/utils/kakaoMapLoader'
import { useLocationStore } from '@/stores/location'
import { useRecommendStore } from '@/stores/recommend'
import { getHashtagByCode } from '@/constants/hashtags'
import { recommendApi } from '@/api/recommend'
import AppLayout from '@/components/layout/AppLayout.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const locationStore = useLocationStore()
const recommendStore = useRecommendStore()

// Recommend에서 전달받은 태그 정보
const selectedTags = ref([])
const selectedMealType = ref(null)

// 선택된 태그 라벨
const selectedTagLabels = computed(() => {
  return selectedTags.value.map(code => {
    const tag = getHashtagByCode(code)
    return tag ? tag.label : code
  })
})

// 상태
const mapContainer = ref(null)
const radius = ref(250)
const isLoading = ref(false)
const hasLocation = ref(false)
const isMapLoading = ref(true)
const mapError = ref(null)

// 카카오맵 관련
let map = null
let circle = null
let marker = null
const currentPosition = ref({
  latitude: 37.5665,
  longitude: 126.9780
})

// 고정 줌 레벨
const FIXED_ZOOM_LEVEL = 5

// 카카오맵 초기화
const initMap = async () => {
  isMapLoading.value = true
  mapError.value = null
  
  try {
    // mapContainer가 준비될 때까지 대기
    await nextTick()
    
    if (!mapContainer.value) {
      throw new Error('맵 컨테이너가 준비되지 않았습니다.')
    }
    
    console.log('🗺️ 카카오맵 로드 시작...')
    const kakao = await loadKakaoMap()
    console.log('🗺️ 카카오맵 로드 완료:', kakao)
    
    const options = {
      center: new kakao.maps.LatLng(currentPosition.value.latitude, currentPosition.value.longitude),
      level: FIXED_ZOOM_LEVEL,
      draggable: true,
      scrollwheel: false,
      disableDoubleClickZoom: true
    }
    
    map = new kakao.maps.Map(mapContainer.value, options)
    map.setZoomable(false)
    
    // 현재 위치 마커 생성
    marker = new kakao.maps.Marker({
      position: map.getCenter(),
      map: map
    })
    
    // 범위 원 생성
    circle = new kakao.maps.Circle({
      center: map.getCenter(),
      radius: radius.value,
      strokeWeight: 2,
      strokeColor: '#667eea',
      strokeOpacity: 0.8,
      strokeStyle: 'solid',
      fillColor: '#667eea',
      fillOpacity: 0.2
    })
    circle.setMap(map)
    
    isMapLoading.value = false
    console.log('✅ 카카오맵 초기화 완료')
    
    // 현재 위치 가져오기
    getCurrentLocation()
    
  } catch (error) {
    console.error('❌ 카카오맵 초기화 실패:', error)
    isMapLoading.value = false
    mapError.value = error.message || '지도를 불러오는데 실패했습니다.'
  }
}

// 맵 다시 로드
const retryLoadMap = () => {
  initMap()
}

// 현재 위치 가져오기
const getCurrentLocation = () => {
  if (!navigator.geolocation) {
    ElMessage.warning('이 브라우저에서는 위치 서비스를 지원하지 않습니다.')
    hasLocation.value = true // 기본 위치로 진행
    return
  }
  
  ElMessage.info('현재 위치를 확인하고 있습니다...')
  
  navigator.geolocation.getCurrentPosition(
    (position) => {
      currentPosition.value = {
        latitude: position.coords.latitude,
        longitude: position.coords.longitude
      }
      
      hasLocation.value = true
      
      // locationStore에도 저장
      locationStore.setLocation(
        position.coords.latitude,
        position.coords.longitude
      )
      locationStore.setRadius(radius.value)
      
      updateMapCenter()
      ElMessage.success('현재 위치가 확인되었습니다!')
    },
    (error) => {
      console.error('위치 정보 실패:', error)
      let errorMsg = '위치 정보를 가져올 수 없습니다.'
      
      switch (error.code) {
        case error.PERMISSION_DENIED:
          errorMsg = '위치 권한이 거부되었습니다. 브라우저 설정에서 위치 권한을 허용해주세요.'
          break
        case error.POSITION_UNAVAILABLE:
          errorMsg = '위치 정보를 사용할 수 없습니다.'
          break
        case error.TIMEOUT:
          errorMsg = '위치 확인 시간이 초과되었습니다.'
          break
      }
      
      ElMessage.warning(errorMsg + ' 기본 위치(서울)로 표시됩니다.')
      hasLocation.value = true // 기본 위치로 진행
    },
    {
      enableHighAccuracy: false,
      timeout: 5000,
      maximumAge: 60000
    }
  )
}

// 맵 중심 업데이트
const updateMapCenter = () => {
  if (!map || !window.kakao) return
  
  const kakao = window.kakao
  const newCenter = new kakao.maps.LatLng(
    currentPosition.value.latitude,
    currentPosition.value.longitude
  )
  
  map.setCenter(newCenter)
  if (marker) marker.setPosition(newCenter)
  if (circle) circle.setPosition(newCenter)
}

// 원 크기 업데이트
const updateCircle = () => {
  if (!circle) return
  circle.setRadius(radius.value)
  locationStore.setRadius(radius.value)
}

// 현재 위치로 이동
const moveToCurrentLocation = () => {
  getCurrentLocation()
}

// AI 추천 요청 (태그 + 위치 정보를 백엔드에 전송)
const requestRecommendation = async () => {
  if (!hasLocation.value) {
    ElMessage.warning('위치를 먼저 확인해주세요.')
    return
  }
  
  isLoading.value = true
  
  try {
    // 새로운 추천 시작 전 스토어 초기화
    recommendStore.clearAll()
    
    // 추천 컨텍스트 저장
    recommendStore.setContext({
      latitude: currentPosition.value.latitude,
      longitude: currentPosition.value.longitude,
      radius: radius.value,
      hashTagCodes: selectedTags.value
    })
    
    // 백엔드에 전송할 데이터
    const requestData = {
      latitude: currentPosition.value.latitude,
      longitude: currentPosition.value.longitude,
      radius: radius.value,
      hashTagCodes: selectedTags.value
    }
    
    console.log('📤 AI 추천 요청 데이터:', requestData)
    
    // 실제 API 호출
    let response = null
    try {
      response = await recommendApi.getRecommendation(requestData)
      console.log('📥 AI 추천 응답:', response)
    } catch (apiError) {
      console.warn('API 호출 실패, 더미 데이터 사용:', apiError)
      // API 실패 시 더미 데이터로 진행 (개발용)
      response = {
        recommendId: 'demo_recommend_1',
        restaurantId: 'restaurant_demo',
        restaurantName: '맛있는 식당 (데모)',
        category: '한식 > 백반/가정식',
        phone: '02-1234-5678',
        address: '서울 서초구 서초동 1234-56',
        roadAddress: '서울 서초구 서초대로 123',
        latitude: currentPosition.value.latitude + 0.001,
        longitude: currentPosition.value.longitude + 0.001,
        distance: 150,
        placeUrl: '',
        reason: '선택하신 태그를 기반으로 AI가 추천한 맛집입니다. 실제 백엔드 연동 후 정확한 추천 결과가 표시됩니다.',
        rating: 4.5,
        reviewCount: 23
      }
    }
    
    // 추천된 식당 ID를 제외 목록에 추가
    if (response.restaurantId) {
      recommendStore.addExcludedRestaurant(response.restaurantId)
    }
    
    // Restaurant 페이지로 이동 (결과 데이터와 함께)
    router.push({
      name: 'Restaurant',
      query: {
        lat: currentPosition.value.latitude,
        lng: currentPosition.value.longitude,
        radius: radius.value,
        tags: selectedTags.value.join(',')
      },
      // 추천 결과를 state로 전달
      state: {
        recommendResult: response
      }
    })
    
  } catch (error) {
    console.error('추천 요청 실패:', error)
    ElMessage.error('추천 요청에 실패했습니다. 다시 시도해주세요.')
  } finally {
    isLoading.value = false
  }
}

onMounted(async () => {
  // 쿼리에서 태그 정보 가져오기
  if (route.query.tags) {
    selectedTags.value = route.query.tags.split(',')
  }
  if (route.query.mealType) {
    selectedMealType.value = route.query.mealType
  }
  
  // locationStore에서 기존 위치 가져오기
  if (locationStore.hasLocation) {
    currentPosition.value = {
      latitude: locationStore.latitude,
      longitude: locationStore.longitude
    }
    radius.value = locationStore.radius
    hasLocation.value = true
  }
  
  // 카카오맵 초기화
  await initMap()
})

onUnmounted(() => {
  if (circle) circle.setMap(null)
  if (marker) marker.setMap(null)
})
</script>

<style scoped>
/* 선택된 태그 바 */
.selected-tags-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: white;
  border-bottom: 1px solid #eee;
  flex-wrap: wrap;
}

.tags-label {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

/* 맵 영역 */
.map-section {
  flex: 1;
  position: relative;
  min-height: 300px;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.map-container {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.map-container.hidden {
  visibility: hidden;
}

/* 맵 로딩 */
.map-loading {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  background: #f5f5f5;
  z-index: 10;
}

.loading-icon {
  animation: rotate 1s linear infinite;
  color: #667eea;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.map-loading span {
  font-size: 14px;
  color: #666;
}

/* 맵 에러 */
.map-error {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  background: #f5f5f5;
  z-index: 10;
  padding: 20px;
  text-align: center;
}

.error-icon {
  font-size: 48px;
}

.map-error span {
  font-size: 14px;
  color: #666;
}

.current-location-btn {
  position: absolute;
  bottom: 20px;
  left: 20px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: #fff;
  border: none;
  border-radius: 24px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.2s;
  z-index: 5;
}

.current-location-btn:hover {
  background: #f5f5f5;
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.btn-icon {
  font-size: 16px;
}

/* 컨트롤 영역 */
.control-section {
  padding: 24px 20px 32px;
  background: white;
}

.radius-display {
  text-align: center;
  margin-bottom: 20px;
}

.radius-value {
  display: block;
  font-size: 36px;
  font-weight: 700;
  color: #333;
  line-height: 1;
}

.radius-label {
  display: block;
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}

.slider-container {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.slider-container .el-slider {
  flex: 1;
}

.slider-label {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  min-width: 36px;
}

.recommend-btn {
  width: 100%;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: #333;
  border-color: #333;
}

.recommend-btn:hover:not(:disabled) {
  background: #555;
  border-color: #555;
}

.recommend-btn:disabled {
  background: #ccc;
  border-color: #ccc;
}

/* Element Plus 슬라이더 커스텀 */
:deep(.el-slider__runway) {
  background-color: #e0e0e0;
  height: 8px;
}

:deep(.el-slider__bar) {
  background-color: #667eea;
  height: 8px;
}

:deep(.el-slider__button-wrapper) {
  top: -14px;
}

:deep(.el-slider__button) {
  width: 24px;
  height: 24px;
  border: 3px solid #667eea;
}
</style>
