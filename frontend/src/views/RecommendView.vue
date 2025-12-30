<template>
  <AppLayout title="AI 추천" :show-back="true">
    <div class="recommend-view">
      <!-- 해시태그 선택 -->
      <div class="hashtag-wrapper">
        <h3 class="step-title">오늘 어떤 기분이세요?</h3>
        <p class="step-description">원하는 태그를 선택하면 AI가 맛집을 추천해드려요</p>
        
        <HashTagSelector
          v-model="selectedTags"
          v-model:meal-type="selectedMealType"
        />
      </div>

      <!-- 다음 단계 버튼 -->
      <div class="submit-section">
        <el-button
          type="primary"
          size="large"
          class="submit-btn"
          :disabled="selectedTags.length === 0"
          @click="goToSearch"
        >
          <template v-if="selectedTags.length === 0">
            태그를 선택해주세요
          </template>
          <template v-else>
            위치 선택하기 →
          </template>
        </el-button>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '@/components/layout/AppLayout.vue'
import HashTagSelector from '@/components/recommend/HashTagSelector.vue'
import { useRecommendStore } from '@/stores/recommend'

const router = useRouter()
const recommendStore = useRecommendStore()

// 상태
const selectedTags = ref([])
const selectedMealType = ref(null)

// Search 페이지로 이동 (태그 정보 전달)
const goToSearch = () => {
  if (selectedTags.value.length === 0) return
  
  router.push({
    name: 'Search',
    query: {
      tags: selectedTags.value.join(','),
      mealType: selectedMealType.value
    }
  })
}

// 컴포넌트 마운트 시 추천 스토어 초기화 (새로운 추천 시작)
onMounted(() => {
  recommendStore.clearAll()
})
</script>

<style scoped>
.recommend-view {
  padding: 16px;
  display: flex;
  flex-direction: column;
  min-height: 100%;
}

/* 해시태그 선택 영역 */
.hashtag-wrapper {
  flex: 1;
}

.step-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0 0 4px 0;
}

.step-description {
  font-size: 14px;
  color: #666;
  margin: 0 0 20px 0;
}

/* 제출 섹션 */
.submit-section {
  padding: 16px 0;
  background: linear-gradient(to top, #f5f5f5 0%, transparent 100%);
  position: sticky;
  bottom: 0;
}

.submit-btn {
  width: 100%;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: #333;
  border-color: #333;
}

.submit-btn:hover:not(:disabled) {
  background: #555;
  border-color: #555;
}

.submit-btn:disabled {
  background: #ccc;
  border-color: #ccc;
}
</style>
