<template>
  <div class="app-layout">
    <!-- 헤더 -->
    <header class="app-header">
      <div class="logo">
        <img src="@/assets/logo.png" alt="Food Finder" class="logo-icon" />
        <span class="logo-text">Food Finder</span>
      </div>
    </header>

    <!-- 메인 컨텐츠 영역 -->
    <main class="app-content">
      <slot></slot>
    </main>

    <!-- 하단 영역 (필요 시 사용) -->
    <footer v-if="$slots.footer" class="app-footer">
      <slot name="footer"></slot>
    </footer>
  </div>
</template>

<script setup>
</script>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #fff;
  max-width: 430px;
  margin: 0 auto;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

/* 헤더 */
.app-header {
  padding: 16px 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
  flex-shrink: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-icon {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #FF6B6B;
}

/* 메인 컨텐츠 */
.app-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 하단 영역 */
.app-footer {
  flex-shrink: 0;
  background: #fff;
  border-top: 1px solid #eee;
}
</style>
