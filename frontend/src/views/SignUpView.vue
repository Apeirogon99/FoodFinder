<template>
  <div class="signup-view">
    <div class="signup-container">
      <!-- 헤더 -->
      <div class="signup-header">
        <h1>프로필 설정</h1>
        <p>서비스 이용을 위해 프로필을 설정해주세요</p>
      </div>

      <!-- 폼 -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        class="signup-form"
      >
        <!-- 닉네임 -->
        <el-form-item label="닉네임" prop="nickname">
          <el-input
            v-model="form.nickname"
            placeholder="닉네임을 입력해주세요"
            maxlength="20"
            show-word-limit
            size="large"
          />
        </el-form-item>

        <!-- 알레르기/식이제한 선택 -->
        <el-form-item label="알레르기 정보 (선택)">
          <div class="allergy-section">
            <!-- 알레르기 그룹 -->
            <div
              v-for="(items, groupName) in allergyGroups"
              :key="groupName"
              class="allergy-group"
            >
              <div class="allergy-tags">
                <el-tag
                  v-for="item in items"
                  :key="item.code"
                  :type="isSelected(item.code) ? 'primary' : 'info'"
                  :effect="isSelected(item.code) ? 'dark' : 'plain'"
                  class="allergy-tag"
                  @click="toggleAllergy(item.code)"
                >
                  {{ item.label }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-form-item>

        <!-- 제출 버튼 -->
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="submit-btn"
            :loading="isLoading"
            @click="handleSubmit"
          >
            시작하기
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ALLERGY_TYPES_BY_GROUP } from '@/constants/allergies'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const isLoading = ref(false)

// 폼 데이터
const form = reactive({
  nickname: '',
  selectedAllergies: [],
})

// 유효성 규칙
const rules = {
  nickname: [
    { required: true, message: '닉네임을 입력해주세요', trigger: 'blur' },
    { min: 2, max: 20, message: '2~20자로 입력해주세요', trigger: 'blur' },
  ],
}

// 그룹별 알레르기 목록
const allergyGroups = computed(() => ALLERGY_TYPES_BY_GROUP)

// 알레르기 선택 여부 확인
const isSelected = (code) => {
  return form.selectedAllergies.includes(code)
}

// 알레르기 토글
const toggleAllergy = (code) => {
  const index = form.selectedAllergies.indexOf(code)
  if (index === -1) {
    form.selectedAllergies.push(code)
  } else {
    form.selectedAllergies.splice(index, 1)
  }
}

// 제출 처리
const handleSubmit = async () => {
  try {
    // 폼 유효성 검사
    const valid = await formRef.value.validate()
    if (!valid) return

    isLoading.value = true

    // 회원가입 요청
    const requestData = {
      nickname: form.nickname,
      allergies: form.selectedAllergies.map((code) => ({
        allergyType: code,
      })),
    }

    await userStore.signUp(requestData)

    ElMessage.success('프로필 설정이 완료되었습니다!')
    router.replace('/')
  } catch (error) {
    console.error('회원가입 실패:', error)
    ElMessage.error('프로필 설정에 실패했습니다. 다시 시도해주세요.')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.signup-view {
  min-height: 100vh;
  background: var(--color-bg-grouped, #f2f2f7);
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.signup-container {
  background: var(--color-bg-primary, #ffffff);
  border-radius: 16px;
  padding: 32px 24px;
  width: 100%;
  max-width: 480px;
  box-shadow: var(--ios-card-shadow);
}

/* 헤더 */
.signup-header {
  text-align: center;
  margin-bottom: 32px;
}

.signup-header h1 {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary, #1c1c1e);
  margin: 0 0 8px 0;
  letter-spacing: -0.02em;
}

.signup-header p {
  font-size: 14px;
  color: var(--color-text-secondary, #3c3c43);
  margin: 0;
}

/* 폼 */
.signup-form {
  width: 100%;
}

/* 알레르기 섹션 */
.allergy-section {
  width: 100%;
}

.allergy-group {
  margin-bottom: 16px;
}

.allergy-group:last-child {
  margin-bottom: 0;
}

.group-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--color-text-tertiary, #8e8e93);
  margin: 0 0 8px 0;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.allergy-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.allergy-tag {
  cursor: pointer;
  transition: all 0.2s;
  padding: 8px 16px;
  font-size: 13px;
  border-radius: 20px !important;
}

.allergy-tag:active {
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

/* 제출 버튼 */
.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 14px;
  margin-top: 16px;
  background: var(--color-primary, #007AFF);
  border-color: var(--color-primary, #007AFF);
}

.submit-btn:hover {
  background: var(--color-primary-hover, #0056CC);
  border-color: var(--color-primary-hover, #0056CC);
}
</style>
