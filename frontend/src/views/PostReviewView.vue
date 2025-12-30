<template>
  <AppLayout title="리뷰 작성" :show-back="true" :hide-bottom-nav="true">
    <div class="post-review-view">
      <!-- 음식점 정보 -->
      <div class="restaurant-info">
        <h2 class="restaurant-name">{{ restaurantName }}</h2>
        <p class="restaurant-category">{{ restaurantCategory }}</p>
      </div>

      <!-- 평점 선택 -->
      <div class="rating-section">
        <label class="section-label">평점</label>
        <div class="rating-wrapper">
          <el-rate
            v-model="form.rating"
            :colors="['#FF6B6B', '#FF6B6B', '#FF6B6B']"
            :void-color="'#ddd'"
            size="large"
            show-score
            score-template="{value}점"
          />
        </div>
      </div>

      <!-- 리뷰 내용 -->
      <div class="content-section">
        <label class="section-label">리뷰 내용</label>
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="6"
          placeholder="음식점에 대한 솔직한 리뷰를 작성해주세요"
          maxlength="500"
          show-word-limit
          resize="none"
        />
      </div>

      <!-- 해시태그 선택 (선택사항) -->
      <div class="hashtag-section">
        <label class="section-label">해시태그 (선택)</label>
        <div class="hashtag-list">
          <el-tag
            v-for="tag in quickTags"
            :key="tag"
            :type="form.tags.includes(tag) ? '' : 'info'"
            :effect="form.tags.includes(tag) ? 'dark' : 'plain'"
            class="hashtag-tag"
            @click="toggleTag(tag)"
          >
            {{ tag }}
          </el-tag>
        </div>
      </div>

      <!-- 제출 버튼 -->
      <div class="submit-section">
        <el-button
          type="primary"
          size="large"
          class="submit-btn"
          :loading="isSubmitting"
          :disabled="!canSubmit"
          @click="handleSubmit"
        >
          리뷰 등록하기
        </el-button>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppLayout from '@/components/layout/AppLayout.vue'
import { reviewApi } from '@/api/review'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 음식점 정보
const restaurantId = ref('')
const restaurantName = ref('')
const restaurantCategory = ref('')

// 폼 데이터
const form = reactive({
  rating: 0,
  content: '',
  tags: []
})

// 상태
const isSubmitting = ref(false)

// 빠른 태그
const quickTags = [
  '맛있어요',
  '양이 많아요',
  '가성비 좋아요',
  '분위기 좋아요',
  '친절해요',
  '재방문 의사',
  '웨이팅 있어요',
  '주차 가능'
]

// 제출 가능 여부
const canSubmit = computed(() => {
  return form.rating > 0 && form.content.trim().length >= 10
})

// 태그 토글
const toggleTag = (tag) => {
  const index = form.tags.indexOf(tag)
  if (index === -1) {
    form.tags.push(tag)
  } else {
    form.tags.splice(index, 1)
  }
}

// 리뷰 제출
const handleSubmit = async () => {
  if (!canSubmit.value) {
    ElMessage.warning('평점과 리뷰 내용(10자 이상)을 입력해주세요')
    return
  }

  isSubmitting.value = true

  try {
    await reviewApi.createReview(restaurantId.value, {
      rating: form.rating,
      content: form.content,
      tags: form.tags
    })

    ElMessage.success('리뷰가 등록되었습니다!')
    router.push('/')
  } catch (error) {
    console.error('리뷰 등록 실패:', error)
    ElMessage.error('리뷰 등록에 실패했습니다')
  } finally {
    isSubmitting.value = false
  }
}

onMounted(() => {
  // 쿼리에서 음식점 정보 가져오기
  restaurantId.value = route.query.restaurantId || ''
  restaurantName.value = route.query.restaurantName || '음식점'
  restaurantCategory.value = route.query.category || ''
})
</script>

<style scoped>
.post-review-view {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  min-height: 100%;
}

/* 음식점 정보 */
.restaurant-info {
  text-align: center;
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.restaurant-name {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0 0 4px 0;
}

.restaurant-category {
  font-size: 14px;
  color: #999;
  margin: 0;
}

/* 섹션 공통 */
.section-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

/* 평점 섹션 */
.rating-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.rating-wrapper {
  display: flex;
  justify-content: center;
}

.rating-wrapper :deep(.el-rate) {
  height: auto;
}

.rating-wrapper :deep(.el-rate__icon) {
  font-size: 32px;
}

/* 리뷰 내용 섹션 */
.content-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.content-section :deep(.el-textarea__inner) {
  font-size: 14px;
  line-height: 1.6;
}

/* 해시태그 섹션 */
.hashtag-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.hashtag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hashtag-tag {
  cursor: pointer;
  transition: all 0.2s;
  padding: 8px 12px;
  font-size: 13px;
}

.hashtag-tag:hover {
  transform: scale(1.05);
}

/* 제출 섹션 */
.submit-section {
  margin-top: auto;
  padding: 16px 0;
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
