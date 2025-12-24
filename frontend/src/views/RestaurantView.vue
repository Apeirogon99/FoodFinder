<template>
  <AppLayout>
    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="loading-section">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 메인 컨텐츠 -->
    <template v-else>
      <!-- 카카오맵 영역 -->
      <div class="map-section">
        <div ref="mapContainer" class="map-container"></div>
      </div>

      <!-- 음식점 정보 카드 -->
      <div class="restaurant-card">
        <!-- 이름 & 평점 -->
        <div class="card-header">
          <h2 class="restaurant-name">{{ restaurant.name || 'Restaurant' }}</h2>
          <div class="rating-badge">
            <span class="rating-value">{{ formattedRating }}</span>
          </div>
        </div>

        <!-- 상세 정보 -->
        <div class="info-list">
          <div class="info-item">
            <span class="info-label">Category</span>
            <span class="info-value">{{ restaurant.category || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">Phone</span>
            <span class="info-value">{{ restaurant.phone || '-' }}</span>
          </div>
          <div class="info-row">
            <div class="info-item half">
              <span class="info-label">Address</span>
              <span class="info-value truncate">{{ shortAddress }}</span>
            </div>
            <div class="info-item half">
              <span class="info-label">Distance</span>
              <span class="info-value">{{ formattedDistance }}</span>
            </div>
          </div>
        </div>

        <!-- AI 추천 이유 -->
        <div class="ai-recommend-section">
          <label class="recommend-label">AI 추천 이유</label>
          <div class="recommend-content">
            {{ restaurant.recommend || '맛있는 음식점입니다!' }}
          </div>
        </div>

        <!-- 버튼 영역 -->
        <div class="button-group">
          <el-button class="btn-secondary" @click="requestAnotherRecommend">
            다른 추천
          </el-button>
          <el-button type="primary" class="btn-primary" @click="goToWriteReview">
            리뷰 작성
          </el-button>
        </div>
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { loadKakaoMap } from '@/utils/kakaoMapLoader'
import AppLayout from '@/components/layout/AppLayout.vue'

const route = useRoute()
const router = useRouter()

// 상태
const mapContainer = ref(null)
const isLoading = ref(true)

// 음식점 데이터
const restaurant = ref({
  id: '',
  name: '',
  category: '',
  phone: '',
  address: '',
  roadAddress: '',
  latitude: 37.5665,
  longitude: 126.9780,
  distance: 0,
  placeUrl: '',
  recommend: '',
  rating: 0
})

// 카카오맵 관련
let map = null
let marker = null

// Computed
const formattedRating = computed(() => {
  const rating = restaurant.value.rating || 0
  return rating.toFixed(1)
})

const formattedDistance = computed(() => {
  const distance = restaurant.value.distance || 0
  if (distance >= 1000) {
    return `${(distance / 1000).toFixed(1)}km`
  }
  return `${Math.round(distance)}m`
})

const shortAddress = computed(() => {
  const addr = restaurant.value.roadAddress || restaurant.value.address || '-'
  if (addr.length > 20) {
    return addr.substring(0, 20) + '...'
  }
  return addr
})

// 카카오맵 초기화
const initMap = async () => {
  try {
    // mapContainer가 준비될 때까지 대기
    if (!mapContainer.value) {
      console.warn('맵 컨테이너가 아직 준비되지 않았습니다.')
      return
    }
    
    const kakao = await loadKakaoMap()
    
    const options = {
      center: new kakao.maps.LatLng(restaurant.value.latitude, restaurant.value.longitude),
      level: 3,
      draggable: false,
      scrollwheel: false,
      disableDoubleClickZoom: true
    }
    
    map = new kakao.maps.Map(mapContainer.value, options)
    map.setZoomable(false)
    
    // 음식점 위치 마커 생성
    const markerPosition = new kakao.maps.LatLng(
      restaurant.value.latitude,
      restaurant.value.longitude
    )
    
    const imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png'
    const imageSize = new kakao.maps.Size(24, 35)
    const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize)
    
    marker = new kakao.maps.Marker({
      position: markerPosition,
      map: map,
      image: markerImage
    })
    
    const infowindow = new kakao.maps.InfoWindow({
      content: `<div style="padding:5px;font-size:12px;font-weight:bold;">${restaurant.value.name}</div>`
    })
    infowindow.open(map, marker)
    
  } catch (error) {
    console.error('카카오맵 초기화 실패:', error)
    ElMessage.error('지도를 불러오는데 실패했습니다.')
  }
}

// 맵 위치 업데이트
const updateMapPosition = () => {
  if (!map || !marker) return
  
  const kakao = window.kakao
  const newPosition = new kakao.maps.LatLng(
    restaurant.value.latitude,
    restaurant.value.longitude
  )
  
  map.setCenter(newPosition)
  marker.setPosition(newPosition)
}

// 음식점 데이터 로드
const loadRestaurantData = async () => {
  isLoading.value = true
  
  try {
    // TODO: 실제 API 호출로 대체
    await new Promise(resolve => setTimeout(resolve, 500))
    
    restaurant.value = {
      id: 'restaurant_001',
      name: '맛있는 식당',
      category: '한식 > 백반/가정식',
      phone: '02-1234-5678',
      address: '서울 서초구 서초동 1234-56',
      roadAddress: '서울 서초구 서초대로 123',
      latitude: parseFloat(route.query.lat) || 37.5665,
      longitude: parseFloat(route.query.lng) || 126.9780,
      distance: 150,
      placeUrl: 'https://place.map.kakao.com/12345678',
      recommend: '근처에서 가장 평점이 높은 한식당입니다. 점심 특선 백반이 인기 메뉴이며, 깔끔한 반찬과 정갈한 맛이 특징입니다.',
      rating: 4.5
    }
    
    // 1. 로딩 해제
    isLoading.value = false
    
    // 2. DOM 업데이트 대기 (v-else가 렌더링되도록)
    await nextTick()
    
    // 3. DOM이 준비된 후 맵 초기화
    await initMap()
    
  } catch (error) {
    console.error('음식점 데이터 로드 실패:', error)
    ElMessage.error('음식점 정보를 불러오는데 실패했습니다.')
    isLoading.value = false
  }
}

// 다른 추천 요청
const requestAnotherRecommend = async () => {
  try {
    // TODO: 다른 추천 API 호출
    await new Promise(resolve => setTimeout(resolve, 500))
    
    restaurant.value = {
      ...restaurant.value,
      id: 'restaurant_002',
      name: '새로운 맛집',
      category: '일식 > 초밥/롤',
      phone: '02-9876-5432',
      recommend: '신선한 회와 초밥이 맛있는 일식당입니다. 런치 세트가 가성비 좋습니다.',
      rating: 4.8,
      distance: 230
    }
    
    updateMapPosition()
    ElMessage.success('새로운 추천을 받았습니다!')
    
  } catch (error) {
    console.error('추천 요청 실패:', error)
    ElMessage.error('추천 요청에 실패했습니다.')
  }
}

// 리뷰 작성 페이지로 이동
const goToWriteReview = () => {
  router.push({
    name: 'Reviews',
    query: {
      restaurantId: restaurant.value.id,
      restaurantName: restaurant.value.name
    }
  })
}

onMounted(() => {
  loadRestaurantData()
})

onUnmounted(() => {
  if (marker) marker.setMap(null)
})
</script>

<style scoped>
/* 로딩 */
.loading-section {
  flex: 1;
  padding: 20px;
}

/* 맵 영역 */
.map-section {
  height: 180px;
  flex-shrink: 0;
}

.map-container {
  width: 100%;
  height: 100%;
}

/* 음식점 카드 */
.restaurant-card {
  flex: 1;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.restaurant-name {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.rating-badge {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 48px;
  height: 32px;
  padding: 0 12px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.rating-value {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

/* 정보 리스트 */
.info-list {
  margin-bottom: 16px;
}

.info-item {
  margin-bottom: 8px;
}

.info-row {
  display: flex;
  gap: 16px;
}

.info-item.half {
  flex: 1;
}

.info-label {
  display: block;
  font-size: 12px;
  font-weight: 500;
  color: #999;
  margin-bottom: 2px;
}

.info-value {
  display: block;
  font-size: 14px;
  color: #333;
}

.info-value.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* AI 추천 영역 */
.ai-recommend-section {
  margin-bottom: 20px;
}

.recommend-label {
  display: block;
  font-size: 12px;
  font-weight: 500;
  color: #999;
  margin-bottom: 8px;
}

.recommend-content {
  padding: 16px;
  background: #f9f9f9;
  border: 1px solid #eee;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.6;
  color: #555;
  min-height: 60px;
}

/* 버튼 그룹 */
.button-group {
  display: flex;
  gap: 12px;
}

.btn-secondary {
  flex: 1;
  height: 44px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  background: #fff;
  border-color: #ddd;
  color: #333;
}

.btn-secondary:hover {
  background: #f5f5f5;
  border-color: #ccc;
}

.btn-primary {
  flex: 1;
  height: 44px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  background: #333;
  border-color: #333;
}

.btn-primary:hover {
  background: #555;
  border-color: #555;
}
</style>
