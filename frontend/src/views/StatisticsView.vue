<template>
  <AppLayout title="활동 통계" :show-back="true">
    <div class="statistics-view">
      <!-- 요약 카드 -->
      <div class="summary-section">
        <div class="summary-card">
          <div class="summary-header">
            <h3>이번 달 활동</h3>
            <span class="period">{{ currentMonth }}</span>
          </div>
          <div class="summary-stats">
            <div class="summary-item">
              <span class="summary-value">{{ stats.monthlyReviews }}</span>
              <span class="summary-label">리뷰</span>
            </div>
            <div class="summary-item">
              <span class="summary-value">{{ stats.monthlyVisits }}</span>
              <span class="summary-label">방문</span>
            </div>
            <div class="summary-item">
              <span class="summary-value">{{ stats.monthlyRecommends }}</span>
              <span class="summary-label">추천</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 차트 섹션: 카테고리별 방문 -->
      <div class="chart-section">
        <h3 class="section-title">카테고리별 방문</h3>
        <div class="chart-card">
          <div class="bar-chart">
            <div 
              v-for="(item, index) in categoryStats" 
              :key="index"
              class="bar-item"
            >
              <span class="bar-label">{{ item.category }}</span>
              <div class="bar-track">
                <div 
                  class="bar-fill" 
                  :style="{ width: item.percentage + '%', backgroundColor: item.color }"
                ></div>
              </div>
              <span class="bar-value">{{ item.count }}회</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 차트 섹션: 평점 분포 -->
      <div class="chart-section">
        <h3 class="section-title">평점 분포</h3>
        <div class="chart-card">
          <div class="rating-chart">
            <div 
              v-for="rating in ratingDistribution" 
              :key="rating.stars"
              class="rating-item"
            >
              <span class="rating-stars">{{ rating.stars }}점</span>
              <div class="rating-track">
                <div 
                  class="rating-fill" 
                  :style="{ width: rating.percentage + '%' }"
                ></div>
              </div>
              <span class="rating-count">{{ rating.count }}</span>
            </div>
          </div>
          <div class="average-rating">
            <span class="avg-label">평균 평점</span>
            <span class="avg-value">{{ stats.averageRating }}</span>
            <el-rate :model-value="parseFloat(stats.averageRating)" disabled allow-half />
          </div>
        </div>
      </div>

      <!-- 최근 방문 음식점 -->
      <div class="recent-section">
        <h3 class="section-title">최근 방문</h3>
        <div class="recent-list">
          <div 
            v-for="restaurant in recentVisits" 
            :key="restaurant.id"
            class="recent-item"
          >
            <div class="recent-info">
              <span class="recent-name">{{ restaurant.name }}</span>
              <span class="recent-category">{{ restaurant.category }}</span>
            </div>
            <div class="recent-meta">
              <span class="recent-rating">★ {{ restaurant.rating }}</span>
              <span class="recent-date">{{ restaurant.visitDate }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import AppLayout from '@/components/layout/AppLayout.vue'

// 현재 월
const currentMonth = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}년 ${now.getMonth() + 1}월`
})

// 통계 데이터
const stats = ref({
  monthlyReviews: 8,
  monthlyVisits: 15,
  monthlyRecommends: 23,
  averageRating: '4.2'
})

// 카테고리별 통계
const categoryStats = ref([
  { category: '한식', count: 12, percentage: 100, color: '#FF6B6B' },
  { category: '일식', count: 8, percentage: 67, color: '#4ECDC4' },
  { category: '중식', count: 6, percentage: 50, color: '#45B7D1' },
  { category: '양식', count: 5, percentage: 42, color: '#96CEB4' },
  { category: '카페', count: 4, percentage: 33, color: '#FFEAA7' }
])

// 평점 분포
const ratingDistribution = ref([
  { stars: 5, count: 8, percentage: 100 },
  { stars: 4, count: 5, percentage: 63 },
  { stars: 3, count: 3, percentage: 38 },
  { stars: 2, count: 1, percentage: 13 },
  { stars: 1, count: 0, percentage: 0 }
])

// 최근 방문
const recentVisits = ref([
  { id: 1, name: '맛있는 한식당', category: '한식', rating: 4.5, visitDate: '12/25' },
  { id: 2, name: '스시 오마카세', category: '일식', rating: 5.0, visitDate: '12/20' },
  { id: 3, name: '피자 팩토리', category: '양식', rating: 3.5, visitDate: '12/15' },
  { id: 4, name: '딤섬 하우스', category: '중식', rating: 4.0, visitDate: '12/10' }
])

// 데이터 로드
const loadStatistics = async () => {
  try {
    // TODO: 실제 API 연결
    // const response = await statisticsApi.getUserStats()
  } catch (error) {
    console.error('통계 로드 실패:', error)
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.statistics-view {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 요약 섹션 */
.summary-section {
  margin-bottom: 8px;
}

.summary-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 20px;
  color: white;
}

.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.summary-header h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.period {
  font-size: 13px;
  opacity: 0.8;
}

.summary-stats {
  display: flex;
  justify-content: space-around;
}

.summary-item {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.summary-value {
  font-size: 32px;
  font-weight: 700;
}

.summary-label {
  font-size: 12px;
  opacity: 0.8;
}

/* 차트 섹션 */
.chart-section {
  margin-bottom: 8px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 4px;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 바 차트 */
.bar-chart {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.bar-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bar-label {
  width: 48px;
  font-size: 13px;
  color: #666;
}

.bar-track {
  flex: 1;
  height: 20px;
  background: #f5f5f5;
  border-radius: 10px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 10px;
  transition: width 0.5s ease;
}

.bar-value {
  width: 40px;
  font-size: 13px;
  font-weight: 500;
  color: #333;
  text-align: right;
}

/* 평점 차트 */
.rating-chart {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 20px;
}

.rating-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rating-stars {
  width: 32px;
  font-size: 13px;
  color: #666;
}

.rating-track {
  flex: 1;
  height: 16px;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
}

.rating-fill {
  height: 100%;
  background: #FFB800;
  border-radius: 8px;
  transition: width 0.5s ease;
}

.rating-count {
  width: 24px;
  font-size: 13px;
  color: #666;
  text-align: right;
}

.average-rating {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.avg-label {
  font-size: 14px;
  color: #666;
}

.avg-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

/* 최근 방문 */
.recent-section {
  margin-bottom: 8px;
}

.recent-list {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.recent-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f5f5f5;
}

.recent-item:last-child {
  border-bottom: none;
}

.recent-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.recent-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.recent-category {
  font-size: 12px;
  color: #999;
}

.recent-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.recent-rating {
  font-size: 13px;
  font-weight: 500;
  color: #FFB800;
}

.recent-date {
  font-size: 12px;
  color: #999;
}
</style>
