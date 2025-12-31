<template>
  <div class="login-view">
    <div class="login-container">
      <!-- 로고 영역 -->
      <div class="logo-section">
        <div class="logo-icon">
          <el-icon :size="48"><Dish /></el-icon>
        </div>
        <h1 class="app-title">Food Finder</h1>
        <p class="app-subtitle">AI가 추천하는 맛집을 찾아보세요</p>
      </div>

      <!-- 로그인 버튼 -->
      <div class="login-section">
        <el-button
          type="primary"
          size="large"
          class="github-login-btn"
          @click="handleGithubLogin"
          :loading="isLoading"
        >
          <el-icon class="github-icon">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
              <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
            </svg>
          </el-icon>
          <span>GitHub으로 로그인</span>
        </el-button>

        <p class="login-description">
          GitHub 계정으로 간편하게 로그인하세요
        </p>
      </div>

      <!-- 기능 소개 -->
      <div class="features-section">
        <div class="feature-item">
          <div class="feature-icon-box">
            <el-icon :size="18"><Promotion /></el-icon>
          </div>
          <span class="feature-text">AI 맞춤 추천</span>
        </div>
        <div class="feature-item">
          <div class="feature-icon-box">
            <el-icon :size="18"><Location /></el-icon>
          </div>
          <span class="feature-text">내 주변 맛집</span>
        </div>
        <div class="feature-item">
          <div class="feature-icon-box">
            <el-icon :size="18"><Star /></el-icon>
          </div>
          <span class="feature-text">리뷰 & 평점</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { authApi } from '@/api/auth'
import { Dish, Promotion, Location, Star } from '@element-plus/icons-vue'

const isLoading = ref(false)

const handleGithubLogin = () => {
  isLoading.value = true
  window.location.href = authApi.getGithubLoginUrl()
}
</script>

<style scoped>
.login-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-grouped, #f2f2f7);
  padding: 20px;
}

.login-container {
  background: var(--color-bg-primary, #ffffff);
  border-radius: 20px;
  padding: 48px 32px;
  width: 100%;
  max-width: 400px;
  box-shadow: var(--ios-card-shadow, 0 2px 8px rgba(0, 0, 0, 0.08));
  text-align: center;
}

/* 로고 섹션 */
.logo-section {
  margin-bottom: 40px;
}

.logo-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-secondary, #f2f2f7);
  border-radius: 20px;
  color: var(--color-primary, #007AFF);
}

.app-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary, #1c1c1e);
  margin: 0 0 8px 0;
  letter-spacing: -0.02em;
}

.app-subtitle {
  font-size: 15px;
  color: var(--color-text-tertiary, #8e8e93);
  margin: 0;
}

/* 로그인 섹션 */
.login-section {
  margin-bottom: 40px;
}

.github-login-btn {
  width: 100%;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 14px;
  background: var(--color-text-primary, #1c1c1e);
  border-color: var(--color-text-primary, #1c1c1e);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.github-login-btn:hover {
  background: #3c3c43;
  border-color: #3c3c43;
}

.github-icon {
  display: flex;
  align-items: center;
}

.login-description {
  font-size: 13px;
  color: var(--color-text-tertiary, #8e8e93);
  margin-top: 16px;
}

/* 기능 소개 섹션 */
.features-section {
  display: flex;
  justify-content: space-around;
  padding-top: 24px;
  border-top: 0.5px solid var(--color-separator, #e5e5ea);
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.feature-icon-box {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-secondary, #f2f2f7);
  border-radius: 12px;
  color: var(--color-primary, #007AFF);
}

.feature-text {
  font-size: 12px;
  color: var(--color-text-tertiary, #8e8e93);
  font-weight: 500;
}
</style>
