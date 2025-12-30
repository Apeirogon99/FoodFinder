<template>
  <AppLayout title="내 리뷰">
    <div class="reviews-view">
      <!-- 리뷰 통계 헤더 -->
      <div class="stats-header">
        <div class="stat-item">
          <span class="stat-value">{{ reviews.length }}</span>
          <span class="stat-label">작성한 리뷰</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ averageRating }}</span>
          <span class="stat-label">평균 평점</span>
        </div>
      </div>

      <!-- 리뷰 목록 -->
      <div class="review-list">
        <!-- 로딩 상태 -->
        <div v-if="isLoading" class="loading-state">
          <el-skeleton v-for="i in 3" :key="i" :rows="3" animated style="margin-bottom: 16px;" />
        </div>

        <!-- 빈 상태 -->
        <div v-else-if="reviews.length === 0" class="empty-state">
          <span class="empty-icon">📝</span>
          <h3>작성한 리뷰가 없습니다</h3>
          <p>맛집을 방문하고 첫 리뷰를 작성해보세요!</p>
          <el-button type="primary" @click="goToRecommend">
            맛집 추천받기
          </el-button>
        </div>

        <!-- 리뷰 카드 목록 -->
        <template v-else>
          <div 
            v-for="review in reviews" 
            :key="review.id" 
            class="review-card"
            @click="openReviewDetail(review)"
          >
            <!-- 음식점 정보 -->
            <div class="review-header">
              <div class="restaurant-info">
                <h4 class="restaurant-name">{{ review.restaurantName }}</h4>
                <span class="review-date">{{ formatDate(review.createdAt) }}</span>
              </div>
              <div class="rating-badge">
                <span class="rating-star">★</span>
                <span class="rating-value">{{ review.rating.toFixed(1) }}</span>
              </div>
            </div>

            <!-- 리뷰 내용 -->
            <p class="review-content">{{ truncateContent(review.content) }}</p>

            <!-- 액션 버튼 -->
            <div class="review-actions">
              <el-button 
                type="text" 
                size="small"
                @click.stop="openEditModal(review)"
              >
                수정
              </el-button>
              <el-button 
                type="text" 
                size="small"
                class="delete-btn"
                @click.stop="confirmDelete(review)"
              >
                삭제
              </el-button>
            </div>
          </div>

          <!-- 더보기 버튼 -->
          <div v-if="hasMore" class="load-more">
            <el-button 
              :loading="isLoadingMore"
              @click="loadMore"
            >
              더보기
            </el-button>
          </div>
        </template>
      </div>
    </div>

    <!-- 리뷰 상세/수정 모달 -->
    <el-dialog
      v-model="showDetailModal"
      :title="isEditMode ? '리뷰 수정' : '리뷰 상세'"
      width="90%"
      :close-on-click-modal="!isEditMode"
    >
      <template v-if="selectedReview">
        <!-- 음식점 정보 -->
        <div class="modal-restaurant">
          <h3>{{ selectedReview.restaurantName }}</h3>
          <span class="modal-date">{{ formatDate(selectedReview.createdAt) }}</span>
        </div>

        <!-- 평점 -->
        <div class="modal-rating">
          <label>평점</label>
          <el-rate
            v-model="editForm.rating"
            :disabled="!isEditMode"
            show-score
            :colors="['#FF6B6B', '#FF6B6B', '#FF6B6B']"
          />
        </div>

        <!-- 리뷰 내용 -->
        <div class="modal-content">
          <label>리뷰 내용</label>
          <el-input
            v-if="isEditMode"
            v-model="editForm.content"
            type="textarea"
            :rows="5"
            placeholder="리뷰 내용을 입력하세요"
            maxlength="500"
            show-word-limit
          />
          <p v-else class="content-text">{{ selectedReview.content }}</p>
        </div>
      </template>

      <template #footer>
        <div class="modal-footer">
          <el-button @click="closeDetailModal">닫기</el-button>
          <template v-if="isEditMode">
            <el-button 
              type="primary" 
              :loading="isSaving"
              @click="saveReview"
            >
              저장
            </el-button>
          </template>
          <template v-else>
            <el-button type="primary" @click="startEdit">수정하기</el-button>
          </template>
        </div>
      </template>
    </el-dialog>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '@/components/layout/AppLayout.vue'
import { reviewApi } from '@/api/review'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// 상태
const reviews = ref([])
const isLoading = ref(true)
const isLoadingMore = ref(false)
const hasMore = ref(false)
const cursor = ref(null)

// 모달 상태
const showDetailModal = ref(false)
const selectedReview = ref(null)
const isEditMode = ref(false)
const isSaving = ref(false)
const editForm = ref({
  rating: 0,
  content: ''
})

// 평균 평점 계산
const averageRating = computed(() => {
  if (reviews.value.length === 0) return '0.0'
  const sum = reviews.value.reduce((acc, r) => acc + r.rating, 0)
  return (sum / reviews.value.length).toFixed(1)
})

// 날짜 포맷
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 내용 잘라내기
const truncateContent = (content) => {
  if (!content) return ''
  if (content.length > 80) {
    return content.substring(0, 80) + '...'
  }
  return content
}

// 리뷰 목록 로드
const loadReviews = async () => {
  isLoading.value = true
  
  try {
    const response = await reviewApi.getMyReviews()
    reviews.value = response.content || []
    hasMore.value = response.hasNext || false
    cursor.value = response.nextCursor || null
  } catch (error) {
    console.error('리뷰 로드 실패:', error)
    // 개발용 더미 데이터
    reviews.value = [
      {
        id: 1,
        restaurantId: 'rest_001',
        restaurantName: '맛있는 한식당',
        rating: 4.5,
        content: '정말 맛있었어요! 특히 된장찌개가 일품이었습니다. 반찬도 다양하고 양도 넉넉해서 만족스러웠어요.',
        createdAt: '2024-12-25T12:00:00'
      },
      {
        id: 2,
        restaurantId: 'rest_002',
        restaurantName: '스시 오마카세',
        rating: 5.0,
        content: '신선한 회와 초밥이 정말 훌륭했습니다. 가격대비 퀄리티가 최고예요.',
        createdAt: '2024-12-20T18:30:00'
      },
      {
        id: 3,
        restaurantId: 'rest_003',
        restaurantName: '피자 팩토리',
        rating: 3.5,
        content: '피자는 괜찮았는데 파스타는 좀 아쉬웠어요.',
        createdAt: '2024-12-15T19:00:00'
      }
    ]
    hasMore.value = false
  } finally {
    isLoading.value = false
  }
}

// 더 불러오기
const loadMore = async () => {
  if (!hasMore.value || isLoadingMore.value) return
  
  isLoadingMore.value = true
  
  try {
    const response = await reviewApi.getMyReviews({ cursor: cursor.value })
    reviews.value.push(...(response.content || []))
    hasMore.value = response.hasNext || false
    cursor.value = response.nextCursor || null
  } catch (error) {
    console.error('더 불러오기 실패:', error)
  } finally {
    isLoadingMore.value = false
  }
}

// 리뷰 상세 열기
const openReviewDetail = (review) => {
  selectedReview.value = review
  editForm.value = {
    rating: review.rating,
    content: review.content
  }
  isEditMode.value = false
  showDetailModal.value = true
}

// 수정 모달 열기
const openEditModal = (review) => {
  selectedReview.value = review
  editForm.value = {
    rating: review.rating,
    content: review.content
  }
  isEditMode.value = true
  showDetailModal.value = true
}

// 수정 모드 시작
const startEdit = () => {
  isEditMode.value = true
}

// 모달 닫기
const closeDetailModal = () => {
  showDetailModal.value = false
  selectedReview.value = null
  isEditMode.value = false
}

// 리뷰 저장
const saveReview = async () => {
  if (!editForm.value.content.trim()) {
    ElMessage.warning('리뷰 내용을 입력해주세요')
    return
  }
  
  isSaving.value = true
  
  try {
    await reviewApi.updateReview(selectedReview.value.id, {
      rating: editForm.value.rating,
      content: editForm.value.content
    })
    
    // 로컬 상태 업데이트
    const index = reviews.value.findIndex(r => r.id === selectedReview.value.id)
    if (index !== -1) {
      reviews.value[index] = {
        ...reviews.value[index],
        rating: editForm.value.rating,
        content: editForm.value.content
      }
    }
    
    ElMessage.success('리뷰가 수정되었습니다')
    closeDetailModal()
  } catch (error) {
    console.error('리뷰 수정 실패:', error)
    ElMessage.error('리뷰 수정에 실패했습니다')
  } finally {
    isSaving.value = false
  }
}

// 리뷰 삭제 확인
const confirmDelete = (review) => {
  ElMessageBox.confirm(
    '이 리뷰를 삭제하시겠습니까?',
    '리뷰 삭제',
    {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'warning'
    }
  ).then(() => {
    deleteReview(review)
  }).catch(() => {})
}

// 리뷰 삭제
const deleteReview = async (review) => {
  try {
    await reviewApi.deleteReview(review.id)
    reviews.value = reviews.value.filter(r => r.id !== review.id)
    ElMessage.success('리뷰가 삭제되었습니다')
  } catch (error) {
    console.error('리뷰 삭제 실패:', error)
    ElMessage.error('리뷰 삭제에 실패했습니다')
  }
}

// 추천 페이지로 이동
const goToRecommend = () => {
  router.push('/recommend')
}

onMounted(() => {
  loadReviews()
})
</script>

<style scoped>
.reviews-view {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 통계 헤더 */
.stats-header {
  display: flex;
  gap: 16px;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-item {
  flex: 1;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

/* 리뷰 목록 */
.review-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 로딩 상태 */
.loading-state {
  padding: 16px 0;
}

/* 빈 상태 */
.empty-state {
  text-align: center;
  padding: 48px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.empty-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  color: #333;
  margin: 0 0 8px 0;
}

.empty-state p {
  font-size: 14px;
  color: #666;
  margin: 0 0 20px 0;
}

/* 리뷰 카드 */
.review-card {
  background: white;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.review-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.restaurant-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.restaurant-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.review-date {
  font-size: 12px;
  color: #999;
}

.rating-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  background: #FFF9E6;
  padding: 4px 10px;
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

.review-content {
  font-size: 14px;
  color: #555;
  line-height: 1.6;
  margin: 0 0 12px 0;
}

.review-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
}

.delete-btn {
  color: #ff4d4f;
}

/* 더보기 버튼 */
.load-more {
  text-align: center;
  padding: 8px 0;
}

/* 모달 스타일 */
.modal-restaurant {
  margin-bottom: 20px;
}

.modal-restaurant h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.modal-date {
  font-size: 12px;
  color: #999;
}

.modal-rating {
  margin-bottom: 20px;
}

.modal-rating label,
.modal-content label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #666;
  margin-bottom: 8px;
}

.modal-content {
  margin-bottom: 20px;
}

.content-text {
  font-size: 14px;
  color: #333;
  line-height: 1.8;
  margin: 0;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
