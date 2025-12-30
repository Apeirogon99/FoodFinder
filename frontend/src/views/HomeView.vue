<template>
  <AppLayout title="Food Finder">
    <div class="home-view">
      <!-- 환영 메시지 -->
      <section class="welcome-section">
        <h2 class="welcome-title">
          안녕하세요{{ nickname ? `, ${nickname}님` : '' }}! 👋
        </h2>
        <p class="welcome-subtitle">오늘은 어떤 맛집을 찾아볼까요?</p>
      </section>

      <!-- 주요 기능 카드 -->
      <section class="feature-section">
        <h3 class="section-title">빠른 기능</h3>
        
        <div class="feature-grid">
          <!-- AI 추천 -->
          <div class="feature-card primary" @click="goTo('/recommend')">
            <div class="feature-icon">🤖</div>
            <div class="feature-content">
              <h4>AI 맛집 추천</h4>
              <p>오늘 기분에 맞는 맛집을<br/>AI가 추천해드려요</p>
            </div>
            <el-icon class="feature-arrow"><ArrowRight /></el-icon>
          </div>

          <!-- 내 리뷰 -->
          <div class="feature-card" @click="goTo('/reviews')">
            <div class="feature-icon">⭐</div>
            <div class="feature-content">
              <h4>내 리뷰 관리</h4>
              <p>작성한 리뷰를<br/>확인하고 관리하세요</p>
            </div>
            <el-icon class="feature-arrow"><ArrowRight /></el-icon>
          </div>

          <!-- 통계 보기 -->
          <div class="feature-card" @click="goTo('/statistics')">
            <div class="feature-icon">📊</div>
            <div class="feature-content">
              <h4>활동 통계</h4>
              <p>나의 맛집 활동을<br/>한눈에 확인하세요</p>
            </div>
            <el-icon class="feature-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </section>

      <!-- 통계 미리보기 -->
      <section class="stats-section">
        <h3 class="section-title">나의 활동</h3>
        <div class="stats-grid">
          <div class="stat-item" @click="goTo('/statistics')">
            <span class="stat-value">{{ stats.reviewCount }}</span>
            <span class="stat-label">리뷰 작성</span>
          </div>
          <div class="stat-item" @click="goTo('/statistics')">
            <span class="stat-value">{{ stats.visitCount }}</span>
            <span class="stat-label">방문 맛집</span>
          </div>
          <div class="stat-item" @click="goTo('/statistics')">
            <span class="stat-value">{{ stats.recommendCount }}</span>
            <span class="stat-label">AI 추천</span>
          </div>
        </div>
      </section>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight } from '@element-plus/icons-vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 닉네임
const nickname = computed(() => userStore.nickname)

// 통계 데이터
const stats = ref({
  reviewCount: 0,
  visitCount: 0,
  recommendCount: 0
})

// 페이지 이동
const goTo = (path) => {
  router.push(path)
}

// 통계 로드
const loadStats = async () => {
  try {
    // TODO: 실제 API 연결
    stats.value = {
      reviewCount: 12,
      visitCount: 28,
      recommendCount: 45
    }
  } catch (error) {
    console.error('통계 로드 실패:', error)
  }
}

// 마운트 시 초기화
onMounted(async () => {
  // 프로필이 없으면 가져오기
  if (!userStore.profile) {
    try {
      await userStore.fetchProfile()
    } catch (error) {
      console.error('프로필 로드 실패:', error)
    }
  }
  
  // 통계 로드
  loadStats()
})
</script>

<style scoped>
.home-view {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 환영 섹션 */
.welcome-section {
  padding: 8px 0;
}

.welcome-title {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin: 0 0 4px 0;
}

.welcome-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

/* 기능 섹션 */
.feature-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.feature-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feature-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: white;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.feature-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.feature-card.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.feature-card.primary .feature-content h4,
.feature-card.primary .feature-content p {
  color: white;
}

.feature-card.primary .feature-arrow {
  color: white;
}

.feature-icon {
  font-size: 32px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 12px;
}

.feature-card.primary .feature-icon {
  background: rgba(255, 255, 255, 0.2);
}

.feature-content {
  flex: 1;
}

.feature-content h4 {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.feature-content p {
  font-size: 12px;
  color: #666;
  margin: 0;
  line-height: 1.4;
}

.feature-arrow {
  color: #ccc;
}

/* 통계 섹션 */
.stats-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.stat-item {
  background: white;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 4px;
  cursor: pointer;
  transition: transform 0.2s;
}

.stat-item:hover {
  transform: translateY(-2px);
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #667eea;
}

.stat-label {
  font-size: 12px;
  color: #666;
}
</style>
