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
            :colors="['#ff9500', '#ff9500', '#ff9500']"
            :void-color="'#e5e5ea'"
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

      <!-- 해시태그 -->
      <div class="hashtag-section">
        <label class="section-label">해시태그 (선택)</label>
        <div class="hashtag-list">
          <el-tag
            v-for="tag in quickTags"
            :key="tag"
            :type="form.tags.includes(tag) ? 'primary' : 'info'"
            effect="dark"
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

/* 음식점 정보 */
const recommendId = ref('')
const restaurantName = ref('')
const restaurantCategory = ref('')

/* 폼 */
const form = reactive({
  rating: 0,
  content: '',
  tags: []
})

/* 상태 */
const isSubmitting = ref(false)

/* 빠른 태그 */
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

/* 제출 가능 여부 */
const canSubmit = computed(() => {
  return form.rating > 0 && form.content.trim().length >= 10
})

/* 태그 토글 */
const toggleTag = (tag) => {
  const idx = form.tags.indexOf(tag)
  if (idx === -1) {
    form.tags.push(tag)
  } else {
    form.tags.splice(idx, 1)
  }
}

/* 리뷰 제출 */
const handleSubmit = async () => {
  if (!canSubmit.value) {
    ElMessage.warning('평점과 리뷰 내용(10자 이상)을 입력해주세요')
    return
  }

  if (!recommendId.value) {
    ElMessage.error('추천 정보가 없습니다. 다시 시도해주세요.')
    router.replace('/')
    return
  }

  isSubmitting.value = true

  try {
    const payload = {
      rating: form.rating,
      content: form.content
    }

    console.log('📤 리뷰 등록 요청:', { recommendId: recommendId.value, payload })
    await reviewApi.createReview(recommendId.value, payload)

    ElMessage.success('리뷰가 등록되었습니다')
    router.replace('/')

  } catch (e) {
    console.error(e)

    if (e.response?.status === 401) {
      ElMessage.warning('로그인이 필요합니다')
      router.replace('/')
      return
    }

    ElMessage.error('리뷰 등록에 실패했습니다')
  } finally {
    isSubmitting.value = false
  }
}

/* 초기 데이터 */
onMounted(() => {
  recommendId.value = route.query.recommendId || ''
  restaurantName.value = route.query.restaurantName || '음식점'
  restaurantCategory.value = route.query.category || ''
  
  console.log('📋 리뷰 작성 페이지 초기화:', {
    recommendId: recommendId.value,
    restaurantName: restaurantName.value,
    category: restaurantCategory.value
  })
})
</script>

<style scoped>
.post-review-view {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: var(--color-bg-grouped, #f2f2f7);
  min-height: 100%;
}

.restaurant-info,
.rating-section,
.content-section,
.hashtag-section {
  background: var(--color-bg-primary, #ffffff);
  padding: 20px;
  border-radius: 14px;
  box-shadow: var(--ios-card-shadow);
}

.restaurant-name {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text-primary, #1c1c1e);
  margin: 0 0 4px 0;
}

.restaurant-category {
  font-size: 14px;
  color: var(--color-text-secondary, #3c3c43);
  margin: 0;
}

.rating-wrapper {
  padding: 12px 16px;
  background: var(--color-bg-secondary, #f2f2f7);
  border-radius: 12px;
  display: inline-block;
}

.rating-wrapper :deep(.el-rate) {
  height: auto;
}

.rating-wrapper :deep(.el-rate__icon) {
  font-size: 28px;
}

.rating-wrapper :deep(.el-rate__text) {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary, #1c1c1e);
}

.section-label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-tertiary, #8e8e93);
  margin-bottom: 12px;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.hashtag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hashtag-tag {
  cursor: pointer;
  transition: all 0.2s;
  padding: 8px 16px;
  font-size: 13px;
  border-radius: 20px !important;
}

.hashtag-tag:active {
  transform: scale(0.96);
}

/* Element Plus 태그 오버라이드 - 미선택 상태 */
:deep(.el-tag.el-tag--info) {
  background: var(--color-bg-primary, #ffffff) !important;
  border-color: var(--color-separator, #e5e5ea) !important;
  color: var(--color-text-secondary, #3c3c43) !important;
  box-shadow: var(--ios-card-shadow) !important;
}

:deep(.el-tag.el-tag--info:hover) {
  background: var(--color-bg-secondary, #f2f2f7) !important;
  border-color: var(--color-primary, #007AFF) !important;
  color: var(--color-primary, #007AFF) !important;
}

/* Element Plus 태그 오버라이드 - 선택 상태 */
:deep(.el-tag.el-tag--primary.el-tag--dark) {
  background: var(--color-primary, #007AFF) !important;
  border-color: var(--color-primary, #007AFF) !important;
  color: #ffffff !important;
}

.submit-btn {
  width: 100%;
  height: 52px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 14px;
  background: var(--color-primary, #007AFF);
  border-color: var(--color-primary, #007AFF);
}

.submit-btn:hover:not(:disabled) {
  background: var(--color-primary-hover, #0056CC);
  border-color: var(--color-primary-hover, #0056CC);
}

.submit-btn:disabled {
  background: var(--color-bg-tertiary, #e5e5ea);
  border-color: var(--color-bg-tertiary, #e5e5ea);
  color: var(--color-text-tertiary, #8e8e93);
}
</style>
