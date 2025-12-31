<template>
  <AppLayout title="마이페이지">
    <div class="profile-view">
      <!-- 프로필 카드 -->
      <div class="profile-card">
        <div class="profile-header">
          <div class="avatar">
            <img 
              v-if="userStore.profile?.profileImageUrl" 
              :src="userStore.profile.profileImageUrl" 
              alt="프로필"
            />
            <span v-else class="avatar-placeholder">{{ avatarInitial }}</span>
          </div>
          <div class="profile-info">
            <h2 class="nickname">{{ userStore.nickname || '사용자' }}</h2>
            <p class="email">{{ userStore.profile?.email || '' }}</p>
          </div>
        </div>

        <!-- 활동 통계 -->
        <div class="stats-row">
          <div class="stat-item">
            <span class="stat-value">{{ stats.reviewCount }}</span>
            <span class="stat-label">리뷰</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ stats.visitCount }}</span>
            <span class="stat-label">방문</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ stats.recommendCount }}</span>
            <span class="stat-label">추천</span>
          </div>
        </div>
      </div>

      <!-- 메뉴 리스트 -->
      <div class="menu-section">
        <h3 class="section-title">설정</h3>
        
        <div class="menu-list">
          <!-- 프로필 수정 -->
          <div class="menu-item" @click="openEditProfile">
            <div class="menu-icon">
              <el-icon :size="18"><User /></el-icon>
            </div>
            <div class="menu-content">
              <span class="menu-label">프로필 수정</span>
              <span class="menu-desc">닉네임 변경</span>
            </div>
            <el-icon class="menu-arrow"><ArrowRight /></el-icon>
          </div>

          <!-- 알레르기 설정 -->
          <div class="menu-item" @click="openAllergySettings">
            <div class="menu-icon">
              <el-icon :size="18"><Dish /></el-icon>
            </div>
            <div class="menu-content">
              <span class="menu-label">알레르기 설정</span>
              <span class="menu-desc">{{ allergyText }}</span>
            </div>
            <el-icon class="menu-arrow"><ArrowRight /></el-icon>
          </div>

          <!-- 내 리뷰 -->
          <div class="menu-item" @click="goTo('/reviews')">
            <div class="menu-icon">
              <el-icon :size="18"><Star /></el-icon>
            </div>
            <div class="menu-content">
              <span class="menu-label">내 리뷰</span>
              <span class="menu-desc">작성한 리뷰 관리</span>
            </div>
            <el-icon class="menu-arrow"><ArrowRight /></el-icon>
          </div>

          <!-- 통계 -->
          <div class="menu-item" @click="goTo('/statistics')">
            <div class="menu-icon">
              <el-icon :size="18"><DataAnalysis /></el-icon>
            </div>
            <div class="menu-content">
              <span class="menu-label">활동 통계</span>
              <span class="menu-desc">내 활동 분석</span>
            </div>
            <el-icon class="menu-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>

      <!-- 계정 섹션 -->
      <div class="menu-section">
        <h3 class="section-title">계정</h3>
        
        <div class="menu-list">
          <!-- 로그아웃 -->
          <div class="menu-item" @click="handleLogout">
            <div class="menu-icon">
              <el-icon :size="18"><SwitchButton /></el-icon>
            </div>
            <div class="menu-content">
              <span class="menu-label">로그아웃</span>
            </div>
            <el-icon class="menu-arrow"><ArrowRight /></el-icon>
          </div>

          <!-- 회원 탈퇴 -->
          <div class="menu-item danger" @click="confirmWithdraw">
            <div class="menu-icon">
              <el-icon :size="18"><Warning /></el-icon>
            </div>
            <div class="menu-content">
              <span class="menu-label">회원 탈퇴</span>
            </div>
            <el-icon class="menu-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <!-- 프로필 수정 모달 -->
    <el-dialog v-model="showEditModal" title="프로필 수정" width="90%">
      <div class="edit-form">
        <div class="form-group">
          <label>닉네임</label>
          <el-input
            v-model="editForm.nickname"
            placeholder="닉네임을 입력하세요"
            maxlength="20"
            show-word-limit
          />
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showEditModal = false">취소</el-button>
        <el-button type="primary" :loading="isSaving" @click="saveProfile">
          저장
        </el-button>
      </template>
    </el-dialog>

    <!-- 알레르기 설정 모달 -->
    <el-dialog v-model="showAllergyModal" title="알레르기 설정" width="90%">
      <div class="allergy-form">
        <p class="form-desc">해당되는 알레르기를 선택해주세요. AI 추천 시 해당 성분이 포함된 음식을 제외합니다.</p>
        
        <div class="allergy-grid">
          <div
            v-for="allergy in ALLERGIES"
            :key="allergy.code"
            class="allergy-item"
            :class="{ selected: allergyForm.includes(allergy.code) }"
            @click="toggleAllergy(allergy.code)"
          >
            <span class="allergy-icon">{{ allergy.icon }}</span>
            <span class="allergy-name">{{ allergy.label }}</span>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showAllergyModal = false">취소</el-button>
        <el-button type="primary" :loading="isSavingAllergy" @click="saveAllergy">
          저장
        </el-button>
      </template>
    </el-dialog>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, User, Dish, Star, DataAnalysis, SwitchButton, Warning } from '@element-plus/icons-vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'
import { ALLERGIES } from '@/constants/allergies'
import { ElMessage, ElMessageBox } from 'element-plus'
import { statisticsApi } from "@/api/statistics.js"

const router = useRouter()
const userStore = useUserStore()

// 상태
const stats = ref({
  reviewCount: 0,
  visitCount: 0,
  recommendCount: 0
})

// 모달 상태
const showEditModal = ref(false)
const showAllergyModal = ref(false)
const isSaving = ref(false)
const isSavingAllergy = ref(false)

// 수정 폼
const editForm = ref({
  nickname: ''
})

// 알레르기 폼
const allergyForm = ref([])

// 아바타 이니셜
const avatarInitial = computed(() => {
  const nickname = userStore.nickname || '사용자'
  return nickname.charAt(0).toUpperCase()
})

// 알레르기 텍스트
const allergyText = computed(() => {
  const allergies = userStore.profile?.allergies || []
  if (allergies.length === 0) return '설정 안함'

  const labels = allergies.map(item => {
    const code = typeof item === 'object' ? item.allergyType : item
    const allergy = ALLERGIES.find(a => a.code === code)
    return allergy ? allergy.label : code
  })
  
  if (labels.length > 2) {
    return `${labels.slice(0, 2).join(', ')} 외 ${labels.length - 2}개`
  }
  return labels.join(', ')
})

// 페이지 이동
const goTo = (path) => {
  router.push(path)
}

// 프로필 수정 모달 열기
const openEditProfile = () => {
  editForm.value.nickname = userStore.nickname || ''
  showEditModal.value = true
}

// 프로필 저장
const saveProfile = async () => {
  if (!editForm.value.nickname.trim()) {
    ElMessage.warning('닉네임을 입력해주세요')
    return
  }
  
  isSaving.value = true
  
  try {
    await userApi.updateProfile({
      nickname: editForm.value.nickname
    })
    
    // 스토어 업데이트
    userStore.profile.nickname = editForm.value.nickname
    
    ElMessage.success('프로필이 수정되었습니다')
    showEditModal.value = false
  } catch (error) {
    console.error('프로필 수정 실패:', error)
    ElMessage.error('프로필 수정에 실패했습니다')
  } finally {
    isSaving.value = false
  }
}

// 알레르기 설정 모달 열기
const openAllergySettings = () => {
  const currentAllergies = userStore.profile?.allergies || []

  if (currentAllergies.length > 0 && typeof currentAllergies[0] === 'object') {
    allergyForm.value = currentAllergies.map(item => item.allergyType)
  } else {
    allergyForm.value = [...currentAllergies]
  }

  showAllergyModal.value = true
}

// 알레르기 토글
const toggleAllergy = (code) => {
  const index = allergyForm.value.indexOf(code)
  if (index === -1) {
    allergyForm.value.push(code)
  } else {
    allergyForm.value.splice(index, 1)
  }
}

// 알레르기 저장
const saveAllergy = async () => {
  isSavingAllergy.value = true
  
  try {
    const allergyPayload = allergyForm.value.map(type => ({
      allergyType: type
    }))

    await userApi.updateProfile({
      nickname: userStore.nickname,
      allergies: allergyPayload
    })
    
    // 스토어 업데이트
    if (userStore.profile) {
      userStore.profile.allergies = [...allergyForm.value]
    }
    
    ElMessage.success('알레르기 설정이 저장되었습니다')
    showAllergyModal.value = false
  } catch (error) {
    console.error('알레르기 설정 실패:', error)
    ElMessage.error('알레르기 설정에 실패했습니다')
  } finally {
    isSavingAllergy.value = false
  }
}

// 로그아웃
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '로그아웃 하시겠습니까?',
      '로그아웃',
      {
        confirmButtonText: '로그아웃',
        cancelButtonText: '취소',
        type: 'info'
      }
    )
    
    await userStore.logout()
    router.push('/login')
    ElMessage.success('로그아웃되었습니다')
  } catch (error) {
    // 취소 시 무시
  }
}

// 회원 탈퇴 확인
const confirmWithdraw = async () => {
  try {
    await ElMessageBox.confirm(
      '정말로 탈퇴하시겠습니까?\n모든 데이터가 삭제되며 복구할 수 없습니다.',
      '회원 탈퇴',
      {
        confirmButtonText: '탈퇴',
        cancelButtonText: '취소',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    await userApi.deleteAccount()
    await userStore.logout()
    router.push('/login')
    ElMessage.success('회원 탈퇴가 완료되었습니다')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('회원 탈퇴 실패:', error)
      ElMessage.error('회원 탈퇴에 실패했습니다')
    }
  }
}

// 통계 로드
const loadStats = async () => {
  try {
    const response = await statisticsApi.getHomeStats()
    if (response) {
      stats.value = response.data
    }
  } catch (error) {
    console.error('통계 로드 실패:', error)
  }
}

onMounted(() => {
  loadStats()
  
  // 프로필이 없으면 가져오기
  if (!userStore.profile) {
    userStore.fetchProfile()
  }
})
</script>

<style scoped>
.profile-view {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: var(--color-bg-grouped, #f2f2f7);
  min-height: 100%;
}

/* 프로필 카드 */
.profile-card {
  background: var(--color-bg-primary, #ffffff);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--ios-card-shadow, 0 2px 8px rgba(0, 0, 0, 0.08));
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--color-primary, #007AFF);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  font-size: 28px;
  font-weight: 700;
  color: white;
}

.profile-info {
  flex: 1;
}

.nickname {
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text-primary, #1c1c1e);
  margin: 0 0 4px 0;
}

.email {
  font-size: 14px;
  color: var(--color-text-tertiary, #8e8e93);
  margin: 0;
}

.stats-row {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 16px 0;
  background: var(--color-bg-secondary, #f2f2f7);
  border-radius: 12px;
}

.stat-item {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary, #1c1c1e);
}

.stat-label {
  font-size: 12px;
  color: var(--color-text-tertiary, #8e8e93);
}

.stat-divider {
  width: 0.5px;
  height: 32px;
  background: var(--color-separator, #e5e5ea);
}

/* 메뉴 섹션 */
.menu-section {
  background: var(--color-bg-primary, #ffffff);
  border-radius: 16px;
  padding: 8px 0;
  box-shadow: var(--ios-card-shadow, 0 2px 8px rgba(0, 0, 0, 0.08));
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-tertiary, #8e8e93);
  margin: 8px 16px 8px;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.menu-list {
  display: flex;
  flex-direction: column;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.menu-item:active {
  background: var(--color-bg-secondary, #f2f2f7);
}

.menu-item.danger .menu-label {
  color: var(--color-danger, #ff3b30);
}

.menu-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-secondary, #f2f2f7);
  border-radius: 10px;
  color: var(--color-primary, #007AFF);
}

.menu-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.menu-label {
  font-size: 16px;
  font-weight: 400;
  color: var(--color-text-primary, #1c1c1e);
}

.menu-desc {
  font-size: 13px;
  color: var(--color-text-tertiary, #8e8e93);
}

.menu-arrow {
  color: var(--color-text-tertiary, #8e8e93);
  font-size: 16px;
}

/* 수정 폼 */
.edit-form {
  padding: 8px 0;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-primary, #1c1c1e);
  margin-bottom: 8px;
}

/* 알레르기 폼 */
.allergy-form {
  padding: 8px 0;
}

.form-desc {
  font-size: 14px;
  color: var(--color-text-secondary, #3c3c43);
  margin: 0 0 16px 0;
  line-height: 1.6;
}

.allergy-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.allergy-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 14px 8px;
  background: var(--color-bg-secondary, #f2f2f7);
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.allergy-item:active {
  transform: scale(0.96);
}

.allergy-item.selected {
  background: var(--color-primary, #007AFF);
}

.allergy-item.selected .allergy-icon {
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
}

.allergy-item.selected .allergy-name {
  color: #ffffff;
}

.allergy-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-primary, #ffffff);
  border-radius: 8px;
  color: var(--color-text-secondary, #3c3c43);
}

.allergy-name {
  font-size: 12px;
  color: var(--color-text-primary, #1c1c1e);
  text-align: center;
  font-weight: 500;
}
</style>
