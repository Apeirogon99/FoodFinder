<template>
  <AppLayout title="추천 결과" :show-back="true">
    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="loading-section">
      <div class="loading-content">
        <div class="loading-emoji">🍽️</div>
        <el-icon class="loading-icon" :size="32">
          <Loading />
        </el-icon>
        <h3>AI가 맛집을 찾고 있어요</h3>
        <p>잠시만 기다려주세요</p>
      </div>
    </div>

    <!-- 메인 컨텐츠 -->
    <div v-else class="result-container">
      <!-- AI 추천 배지 -->
      <div class="ai-badge">
        <span class="badge-icon">🤖</span>
        <span class="badge-text">AI 추천</span>
      </div>

      <!-- 식당 이름 & 카테고리 -->
      <div class="restaurant-header">
        <h1 class="restaurant-name">{{ restaurant.name || '추천 음식점' }}</h1>
        <p class="restaurant-category">{{ restaurant.category || '맛집' }}</p>
      </div>

      <!-- 평점 & 거리 -->
      <div class="quick-info">
        <div class="info-chip rating">
          <span class="chip-icon">⭐</span>
          <span class="chip-value">{{ formattedRating }}</span>
        </div>
        <div class="info-chip distance">
          <span class="chip-icon">📍</span>
          <span class="chip-value">{{ formattedDistance }}</span>
        </div>
        <div v-if="restaurant.reviewCount" class="info-chip reviews" @click="goToRestaurantReviews">
          <span class="chip-icon">💬</span>
          <span class="chip-value">리뷰 {{ restaurant.reviewCount }}개</span>
        </div>
      </div>

      <!-- 카카오맵 영역 -->
      <div class="map-wrapper">
        <div class="map-section">
          <div v-if="isMapLoading" class="map-loading">
            <el-icon class="loading-icon" :size="24"><Loading /></el-icon>
          </div>
          <div ref="mapContainer" class="map-container"></div>
        </div>
        <div class="address-bar">
          <span class="address-icon">🏠</span>
          <span class="address-text">{{ restaurant.roadAddress || restaurant.address || '주소 정보 없음' }}</span>
        </div>
      </div>

      <!-- AI 추천 이유 -->
      <div class="recommend-section">
        <div class="section-header">
          <span class="section-icon">💡</span>
          <span class="section-title">AI 추천 이유</span>
        </div>
        <p class="recommend-text">{{ restaurant.recommend || '선택하신 조건에 맞는 맛집이에요!' }}</p>
      </div>

      <!-- 연락처 -->
      <div v-if="restaurant.phone" class="contact-section">
        <a :href="'tel:' + restaurant.phone" class="contact-link">
          <span class="contact-icon">📞</span>
          <span class="contact-text">{{ restaurant.phone }}</span>
        </a>
      </div>

      <!-- 버튼 영역 -->
      <div class="action-buttons">
        <button 
          class="btn-retry" 
          :disabled="isReLoading"
          @click="requestReRecommendation"
        >
          <span v-if="isReLoading" class="btn-loading">⏳</span>
          <span v-else class="btn-icon">🔄</span>
          <span class="btn-text">다른 추천</span>
        </button>
        <button class="btn-review" @click="goToWriteReview">
          <span class="btn-icon">✍️</span>
          <span class="btn-text">리뷰 작성</span>
        </button>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Loading } from '@element-plus/icons-vue'
import { loadKakaoMap } from '@/utils/kakaoMapLoader'
import { useRecommendStore } from '@/stores/recommend'
import { recommendApi } from '@/api/recommend'
import AppLayout from '@/components/layout/AppLayout.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const recommendStore = useRecommendStore()

// 상태
const mapContainer = ref(null)
const isLoading = ref(true)
const isReLoading = ref(false)
const isMapLoading = ref(true)

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
  rating: 0,
  reviewCount: 0
})

// 카카오맵 관련
let map = null
let marker = null
let infowindow = null

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

// 카카오맵 초기화
const initMap = async () => {
  isMapLoading.value = true
  
  try {
    await nextTick()
    
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
    
    infowindow = new kakao.maps.InfoWindow({
      content: `<div style="padding:8px 12px;font-size:13px;font-weight:600;white-space:nowrap;">${restaurant.value.name}</div>`
    })
    infowindow.open(map, marker)
    
    isMapLoading.value = false
    
  } catch (error) {
    console.error('카카오맵 초기화 실패:', error)
    isMapLoading.value = false
  }
}

// 음식점 데이터 로드
const loadRestaurantData = async () => {
  isLoading.value = true
  
  try {
    const stateResult = history.state?.recommendResult
    
    if (stateResult) {
      restaurant.value = {
        id: stateResult.id || 'restaurant_001',
        name: stateResult.name || '추천 음식점',
        category: stateResult.category || '',
        phone: stateResult.phone || '',
        address: stateResult.address || '',
        roadAddress: stateResult.roadAddress || '',
        latitude: stateResult.latitude || parseFloat(route.query.lat) || 37.5665,
        longitude: stateResult.longitude || parseFloat(route.query.lng) || 126.9780,
        distance: stateResult.distance || 0,
        placeUrl: stateResult.placeUrl || '',
        recommend: stateResult.recommend || '',
        rating: stateResult.rating || 0,
        reviewCount: stateResult.reviewCount || 0
      }
    } else {
      restaurant.value = {
        id: 'restaurant_demo',
        name: '맛있는 식당 (데모)',
        category: '한식 > 백반/가정식',
        phone: '02-1234-5678',
        address: '서울 서초구 서초동 1234-56',
        roadAddress: '서울 서초구 서초대로 123',
        latitude: parseFloat(route.query.lat) || 37.5665,
        longitude: parseFloat(route.query.lng) || 126.9780,
        distance: 150,
        placeUrl: '',
        recommend: '백엔드 API 연동 후 실제 AI 추천 결과가 표시됩니다.',
        rating: 4.5,
        reviewCount: 23
      }
    }
    
    isLoading.value = false
    await nextTick()
    await initMap()
    
  } catch (error) {
    console.error('음식점 데이터 로드 실패:', error)
    ElMessage.error('추천 정보를 불러오는데 실패했습니다.')
    isLoading.value = false
  }
}

// 재추천 요청
const requestReRecommendation = async () => {
  if (!recommendStore.hasContext) {
    ElMessage.info('추천 조건을 다시 선택해주세요.')
    router.push('/recommend')
    return
  }
  
  isReLoading.value = true
  
  try {
    const requestData = recommendStore.getRecommendRequest
    console.log('📤 재추천 요청 데이터:', requestData)
    
    let response = null
    try {
      response = await recommendApi.getRecommendation(requestData)
      console.log('📥 재추천 응답:', response)
    } catch (apiError) {
      console.warn('API 호출 실패:', apiError)
      
      if (apiError.response?.status === 404 || apiError.response?.data?.message?.includes('없')) {
        ElMessage.warning('주변에 더 이상 추천할 식당이 없습니다.')
        return
      }
      throw apiError
    }
    
    if (response.id) {
      recommendStore.addExcludedRestaurant(response.id)
    }
    
    restaurant.value = {
      id: response.id || 'restaurant_new',
      name: response.name || '추천 음식점',
      category: response.category || '',
      phone: response.phone || '',
      address: response.address || '',
      roadAddress: response.roadAddress || '',
      latitude: response.latitude || restaurant.value.latitude,
      longitude: response.longitude || restaurant.value.longitude,
      distance: response.distance || 0,
      placeUrl: response.placeUrl || '',
      recommend: response.recommend || '',
      rating: response.rating || 0,
      reviewCount: response.reviewCount || 0
    }
    
    await nextTick()
    await updateMapMarker()
    
    ElMessage.success('새로운 맛집을 추천해드렸어요!')
    
  } catch (error) {
    console.error('재추천 요청 실패:', error)
    ElMessage.error('추천 요청에 실패했습니다.')
  } finally {
    isReLoading.value = false
  }
}

// 지도 마커 업데이트
const updateMapMarker = async () => {
  if (!map || !window.kakao) return
  
  try {
    const kakao = window.kakao
    const newPosition = new kakao.maps.LatLng(
      restaurant.value.latitude,
      restaurant.value.longitude
    )
    
    map.setCenter(newPosition)
    if (marker) marker.setPosition(newPosition)
    if (infowindow) {
      infowindow.setContent(`<div style="padding:8px 12px;font-size:13px;font-weight:600;white-space:nowrap;">${restaurant.value.name}</div>`)
    }
  } catch (error) {
    console.error('지도 마커 업데이트 실패:', error)
  }
}

// 리뷰 작성 페이지로 이동
const goToWriteReview = () => {
  router.push({
    name: 'PostReview',
    query: {
      restaurantId: restaurant.value.id,
      restaurantName: restaurant.value.name,
      category: restaurant.value.category
    }
  })
}

// 음식점 리뷰 목록으로 이동
const goToRestaurantReviews = () => {
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
  if (infowindow) infowindow.close()
  if (marker) marker.setMap(null)
})
</script>

<style scoped>
/* 로딩 */
.loading-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100%;
}

.loading-content {
  text-align: center;
  color: white;
}

.loading-emoji {
  font-size: 48px;
  margin-bottom: 16px;
  animation: bounce 1s ease infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.loading-icon {
  animation: rotate 1s linear infinite;
  color: white;
  margin-bottom: 16px;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.loading-content h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.loading-content p {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

/* 결과 컨테이너 */
.result-container {
  padding: 20px;
  background: #f8f9fa;
  min-height: 100%;
}

/* AI 배지 */
.ai-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 12px;
}

.badge-icon {
  font-size: 14px;
}

/* 식당 헤더 */
.restaurant-header {
  margin-bottom: 16px;
}

.restaurant-name {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 4px 0;
  line-height: 1.3;
}

.restaurant-category {
  font-size: 14px;
  color: #666;
  margin: 0;
}

/* 빠른 정보 */
.quick-info {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.info-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: white;
  padding: 8px 12px;
  border-radius: 20px;
  font-size: 13px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.info-chip.reviews {
  cursor: pointer;
  transition: all 0.2s;
}

.info-chip.reviews:hover {
  background: #f0f0f0;
}

.chip-icon {
  font-size: 14px;
}

.chip-value {
  font-weight: 600;
  color: #333;
}

/* 지도 래퍼 */
.map-wrapper {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  margin-bottom: 16px;
}

.map-section {
  width: 100%;
  height: 180px;
  position: relative;
  background: #e9ecef;
}

.map-container {
  width: 100%;
  height: 100%;
}

.map-loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 10;
}

.address-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fafafa;
  border-top: 1px solid #eee;
}

.address-icon {
  font-size: 16px;
}

.address-text {
  font-size: 13px;
  color: #555;
  flex: 1;
}

/* 추천 이유 섹션 */
.recommend-section {
  background: white;
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.section-icon {
  font-size: 18px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.recommend-text {
  font-size: 14px;
  line-height: 1.6;
  color: #555;
  margin: 0;
}

/* 연락처 섹션 */
.contact-section {
  margin-bottom: 16px;
}

.contact-link {
  display: flex;
  align-items: center;
  gap: 10px;
  background: white;
  padding: 14px 16px;
  border-radius: 12px;
  text-decoration: none;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transition: all 0.2s;
}

.contact-link:hover {
  background: #f8f9fa;
}

.contact-icon {
  font-size: 18px;
}

.contact-text {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

/* 액션 버튼 */
.action-buttons {
  display: flex;
  gap: 12px;
  padding-top: 8px;
}

.btn-retry,
.btn-review {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 52px;
  border: none;
  border-radius: 14px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-retry {
  background: white;
  color: #333;
  border: 2px solid #e0e0e0;
}

.btn-retry:hover:not(:disabled) {
  background: #f5f5f5;
  border-color: #ccc;
}

.btn-retry:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-review {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-review:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.btn-icon {
  font-size: 18px;
}

.btn-loading {
  font-size: 18px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.btn-text {
  font-size: 15px;
}
</style>
