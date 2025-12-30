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
              <span class="summary-value">{{ stats.monthlyVisits }}</span>
              <span class="summary-label">방문(승인)</span>
            </div>
            <div class="summary-item">
              <span class="summary-value">{{ stats.monthlyRecommends }}</span>
              <span class="summary-label">총 추천</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 차트 섹션: 카테고리별 추천 -->
      <div class="chart-section">
        <h3 class="section-title">카테고리별 추천</h3>
        <div class="chart-card">
          <div v-if="categoryStats.length > 0" class="bar-chart">
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
          <div v-else class="empty-state">
            데이터가 없습니다.
          </div>
        </div>
      </div>

      <!-- 차트 섹션: 추천 반응 분포 -->
      <div class="chart-section">
        <h3 class="section-title">추천 반응 분포</h3>
        <div class="chart-card">
          <div v-if="reactionStats.length > 0" class="rating-chart">
            <div 
              v-for="item in reactionStats" 
              :key="item.label"
              class="rating-item"
            >
              <span class="rating-stars">{{ item.label }}</span>
              <div class="rating-track">
                <div 
                  class="rating-fill" 
                  :style="{ width: item.percentage + '%', backgroundColor: item.color }"
                ></div>
              </div>
              <span class="rating-count">{{ item.count }}</span>
            </div>
          </div>
           <div v-else class="empty-state">
            데이터가 없습니다.
          </div>
        </div>
      </div>

      <!-- 최근 활동 -->
      <div class="recent-section">
        <h3 class="section-title">최근 활동</h3>
        <div class="recent-list">
          <div 
            v-for="item in recentVisits" 
            :key="item.id"
            class="recent-item"
          >
            <div class="recent-info">
              <span class="recent-name">{{ item.name }}</span>
              <div class="recent-sub">
                <span class="recent-category">{{ item.category }}</span>
              </div>
            </div>
            <div class="recent-meta">
              <span :class="['recent-result', getResultClass(item.result)]">{{ formatResult(item.result) }}</span>
              <span class="recent-date">{{ item.visitDate }}</span>
            </div>
          </div>
          <div v-if="recentVisits.length === 0" class="empty-list">
            최근 활동 내역이 없습니다.
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import { statisticsApi } from '@/api/statistics'

// 현재 월
const currentMonth = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}년 ${now.getMonth() + 1}월`
})

// 통계 데이터
const stats = ref({
  monthlyVisits: 0,
  monthlyRecommends: 0
})

const categoryStats = ref([])
const reactionStats = ref([])
const recentVisits = ref([])

// 색상 팔레트
const categoryColors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7', '#D4A5A5', '#9B59B6']
const reactionConfig = {
  ACCEPTED: { label: '승인', color: '#4ECDC4' },
  REJECTED: { label: '거절', color: '#FF6B6B' },
  PENDING: { label: '보류', color: '#FFEAA7' }
}

// 데이터 로드
const loadStatistics = async () => {
  try {
    const now = new Date()
    const from = new Date(now.getFullYear(), now.getMonth(), 1).toISOString().split('T')[0]
    const to = new Date(now.getFullYear(), now.getMonth() + 1, 0).toISOString().split('T')[0]
    
    const params = { from, to }

    // 병렬로 API 호출
    const [categoriesRes, reactionsRes, recentRes] = await Promise.all([
      statisticsApi.getMyCategoryStats(params),
      statisticsApi.getMyReactionStats(params),
      statisticsApi.getMyRecentStats()
    ])

    // 카테고리 통계 처리
    if (categoriesRes && categoriesRes.data) {
      const total = categoriesRes.data.reduce((sum, item) => sum + item.value, 0)
      categoryStats.value = categoriesRes.data.map((item, index) => ({
        category: item.label,
        count: item.value,
        percentage: total > 0 ? Math.round((item.value / total) * 100) : 0,
        color: categoryColors[index % categoryColors.length]
      })).sort((a, b) => b.count - a.count)
    }

    // 반응 통계 처리
    if (reactionsRes && reactionsRes.data) {
      const total = reactionsRes.data.reduce((sum, item) => sum + item.value, 0)
      
      // 요약 정보 업데이트
      stats.value.monthlyRecommends = total
      const acceptedItem = reactionsRes.data.find(item => item.label === 'ACCEPTED')
      stats.value.monthlyVisits = acceptedItem ? acceptedItem.value : 0

      // 차트 데이터
      reactionStats.value = reactionsRes.data.map(item => {
        const config = reactionConfig[item.label] || { label: item.label, color: '#ccc' }
        return {
          label: config.label,
          count: item.value,
          percentage: total > 0 ? Math.round((item.value / total) * 100) : 0,
          color: config.color
        }
      })
    }

    // 최근 방문 처리
    if (recentRes && recentRes.data) {
      recentVisits.value = recentRes.data
    }

  } catch (error) {
    console.error('통계 로드 실패:', error)
  }
}

const getResultClass = (result) => {
  if (result === 'ACCEPTED') return 'text-success'
  if (result === 'REJECTED') return 'text-danger'
  return 'text-warning'
}

const formatResult = (result) => {
  if (result === 'ACCEPTED') return '승인'
  if (result === 'REJECTED') return '거절'
  return '보류'
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

.empty-state, .empty-list {
  text-align: center;
  color: #999;
  font-size: 13px;
  padding: 20px 0;
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
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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

/* 평점 차트 (재사용) */
.rating-chart {
  display: flex;
  flex-direction: column;
  gap: 8px;
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
  border-radius: 8px;
  transition: width 0.5s ease;
}

.rating-count {
  width: 24px;
  font-size: 13px;
  color: #666;
  text-align: right;
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

.recent-result {
  font-size: 13px;
  font-weight: 500;
}

.text-success { color: #4ECDC4; }
.text-danger { color: #FF6B6B; }
.text-warning { color: #FFEAA7; }

.recent-date {
  font-size: 12px;
  color: #999;
}
</style>
