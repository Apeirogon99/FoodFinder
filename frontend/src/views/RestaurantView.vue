<template>
  <AppLayout title="추천 결과" :show-back="true">
    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="loading-section">
      <div class="loading-content">
        <el-icon class="loading-icon" :size="48">
          <Loading />
        </el-icon>
        <h3>AI가 맛집을 찾고 있어요...</h3>
        <p>잠시만 기다려주세요</p>
      </div>
    </div>

    <!-- 메인 컨텐츠 -->
    <template v-else>
      <!-- 카카오맵 영역 -->
      <div class="map-section">
        <div v-if="isMapLoading" class="map-loading">
          <el-icon class="loading-icon" :size="24"><Loading /></el-icon>
        </div>
        <div ref="mapContainer" class="map-container"></div>
      </div>

      <!-- 음식점 정보 카드 -->
      <div class="restaurant-card">
        <!-- 이름 & 평점 -->
        <div class="card-header">
          <h2 class="restaurant-name">{{ restaurant.name || 'Restaurant' }}</h2>
          <div class="rating-badge">
            <span class="rating-star">★</span>
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

        <!-- 리뷰 미리보기 -->
        <div class="review-preview" @click="goToRestaurantReviews">
          <div class="preview-header">
            <span class="preview-title">리뷰 {{ restaurant.reviewCount || 0 }}개</span>
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <!-- 버튼 영역 -->
        <div class="button-group">
          <el-button class="btn-secondary" @click="goBackToRecommend">
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
import { ArrowRight, Loading } from '@element-plus/icons-vue'
import { loadKakaoMap } from '@/utils/kakaoMapLoader'
import AppLayout from '@/components/layout/AppLayout.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 상태
const mapContainer = ref(null)
const isLoading = ref(true)
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
    
    isMapLoading.value = false
    
  } catch (error) {
    console.error('카카오맵 초기화 실패:', error)
    isMapLoading.value = false
  }
}

// 음식점 데이터 로드 (API 호출 또는 state에서)
const loadRestaurantData = async () => {
  isLoading.value = true
  
  try {
    // history.state에서 추천 결과 확인
    const stateResult = history.state?.recommendResult
    
    if (stateResult) {
      // state에서 결과 사용
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
      // state가 없으면 더미 데이터 사용
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
        recommend: '백엔드 API 연동 후 실제 AI 추천 결과가 표시됩니다. 현재는 데모 데이터입니다.',
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

// 다시 Recommend로 돌아가기 (처음부터 다시)
const goBackToRecommend = () => {
  router.push('/recommend')
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
  padding: 20px;
}

.loading-content {
  text-align: center;
}

.loading-icon {
  animation: rotate 1s linear infinite;
  color: #667eea;
  margin-bottom: 16px;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.loading-content h3 {
  font-size: 18px;
  color: #333;
  margin: 0 0 8px 0;
}

.loading-content p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

/* 맵 영역 */
.map-section {
  height: 180px;
  flex-shrink: 0;
  position: relative;
  background: #f5f5f5;
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
  gap: 4px;
  background: #FFF9E6;
  padding: 6px 12px;
  border-radius: 20px;
}

.rating-star {
  color: #FFB800;
  font-size: 14px;
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
  margin-bottom: 16px;
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
}

/* 리뷰 미리보기 */
.review-preview {
  padding: 14px 16px;
  background: white;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 20px;
  cursor: pointer;
  transition: background 0.2s;
}

.review-preview:hover {
  background: #f9f9f9;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

/* 버튼 그룹 */
.button-group {
  display: flex;
  gap: 12px;
}

.btn-secondary {
  flex: 1;
  height: 48px;
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
  height: 48px;
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
