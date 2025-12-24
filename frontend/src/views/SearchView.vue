<template>
  <AppLayout>
    <!-- 메인 컨텐츠: 카카오맵 영역 -->
    <div class="map-section">
      <div ref="mapContainer" class="map-container"></div>
      
      <!-- 현재 위치 버튼 -->
      <button class="current-location-btn" @click="moveToCurrentLocation">
        현재 위치
      </button>
    </div>

    <!-- 하단 영역 -->
    <template #footer>
      <div class="control-section">
        <!-- 현재 범위 표시 -->
        <div class="radius-display">
          <span class="radius-value">{{ radius }}m</span>
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
          @click="requestRecommendation"
        >
          메뉴 추천 받기
        </el-button>
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { loadKakaoMap } from '@/utils/kakaoMapLoader'
import AppLayout from '@/components/layout/AppLayout.vue'

const router = useRouter()

// 상태
const mapContainer = ref(null)
const radius = ref(250)
const isLoading = ref(false)

// 카카오맵 관련
let map = null
let circle = null
let marker = null
const currentPosition = ref({
  latitude: 37.5665, // 기본값: 서울시청
  longitude: 126.9780
})

// 고정 줌 레벨 (500m 범위가 잘 보이는 레벨)
const FIXED_ZOOM_LEVEL = 5

// 카카오맵 초기화
const initMap = async () => {
  console.log('1. initMap 시작')
  console.log('2. mapContainer.value:', mapContainer.value)
  
  try {
    // mapContainer가 준비될 때까지 대기
    if (!mapContainer.value) {
      console.error('맵 컨테이너가 없습니다!')
      return
    }
    
    console.log('3. 카카오맵 로드 시작')
    const kakao = await loadKakaoMap()
    console.log('4. 카카오맵 로드 완료:', kakao)
    
    const options = {
      center: new kakao.maps.LatLng(currentPosition.value.latitude, currentPosition.value.longitude),
      level: FIXED_ZOOM_LEVEL,
      draggable: false,
      scrollwheel: false,
      disableDoubleClickZoom: true
    }
    
    console.log('5. 맵 생성 시작')
    map = new kakao.maps.Map(mapContainer.value, options)
    console.log('6. 맵 생성 완료:', map)
    
    map.setZoomable(false)
    
    // 현재 위치 마커 생성
    marker = new kakao.maps.Marker({
      position: map.getCenter(),
      map: map
    })
    console.log('7. 마커 생성 완료')
    
    // 범위 원 생성
    circle = new kakao.maps.Circle({
      center: map.getCenter(),
      radius: radius.value,
      strokeWeight: 2,
      strokeColor: '#FF6B6B',
      strokeOpacity: 0.8,
      strokeStyle: 'solid',
      fillColor: '#FFB4B4',
      fillOpacity: 0.4
    })
    circle.setMap(map)
    console.log('8. 원 생성 완료')
    
    getCurrentLocation()
    
  } catch (error) {
    console.error('카카오맵 초기화 실패:', error)
  }
}

// 현재 위치 가져오기
const getCurrentLocation = () => {
  console.log('getCurrentLocation 호출')
  
  if (!navigator.geolocation) {
    console.warn('Geolocation 미지원')
    return
  }
  
  navigator.geolocation.getCurrentPosition(
    (position) => {
      console.log('위치 정보 성공:', position.coords)
      currentPosition.value = {
        latitude: position.coords.latitude,
        longitude: position.coords.longitude
      }
      updateMapCenter()
    },
    (error) => {
      console.error('위치 정보 실패:', error)
    },
    {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 0
    }
  )
}

// 맵 중심 업데이트
const updateMapCenter = () => {
  if (!map) {
    console.warn('맵이 초기화되지 않음')
    return
  }
  
  const kakao = window.kakao
  const newCenter = new kakao.maps.LatLng(
    currentPosition.value.latitude,
    currentPosition.value.longitude
  )
  
  map.setCenter(newCenter)
  if (marker) marker.setPosition(newCenter)
  if (circle) circle.setPosition(newCenter)
  console.log('맵 중심 업데이트 완료')
}

// 원 크기 업데이트
const updateCircle = () => {
  if (!circle) return
  circle.setRadius(radius.value)
}

// 현재 위치로 이동
const moveToCurrentLocation = () => {
  console.log('현재 위치 버튼 클릭')
  getCurrentLocation()
}

// 추천 요청
const requestRecommendation = async () => {
  isLoading.value = true
  
  try {
    const requestData = {
      latitude: currentPosition.value.latitude,
      longitude: currentPosition.value.longitude,
      radius: radius.value
    }
    
    console.log('추천 요청 데이터:', requestData)
    
    // TODO: 실제 API 호출 구현
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    router.push({
      name: 'Restaurant',
      query: {
        lat: currentPosition.value.latitude,
        lng: currentPosition.value.longitude,
        radius: radius.value
      }
    })
    
  } catch (error) {
    console.error('추천 요청 실패:', error)
  } finally {
    isLoading.value = false
  }
}

onMounted(async () => {
  console.log('=== SearchView onMounted ===')
  await nextTick()
  console.log('nextTick 완료, mapContainer:', mapContainer.value)
  await initMap()
})

onUnmounted(() => {
  if (circle) circle.setMap(null)
  if (marker) marker.setMap(null)
})
</script>

<style scoped>
/* 맵 영역 */
.map-section {
  flex: 1;
  position: relative;
  min-height: 400px;
}

.map-container {
  width: 100%;
  height: 100%;
}

.current-location-btn {
  position: absolute;
  bottom: 20px;
  left: 20px;
  padding: 10px 16px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.2s;
}

.current-location-btn:hover {
  background: #f5f5f5;
}

/* 컨트롤 영역 */
.control-section {
  padding: 24px 20px 32px;
}

.radius-display {
  text-align: center;
  margin-bottom: 16px;
}

.radius-value {
  font-size: 32px;
  font-weight: 700;
  color: #333;
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
}

.recommend-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  background: #333;
  border-color: #333;
}

.recommend-btn:hover {
  background: #555;
  border-color: #555;
}

/* Element Plus 슬라이더 커스텀 */
:deep(.el-slider__runway) {
  background-color: #e0e0e0;
}

:deep(.el-slider__bar) {
  background-color: #333;
}

:deep(.el-slider__button) {
  border-color: #333;
}
</style>
