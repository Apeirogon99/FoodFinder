<template>
  <header class="top-header">
    <div class="header-content">
      <!-- 뒤로가기 버튼 (필요시) -->
      <div class="header-left">
        <el-button
          v-if="showBack"
          class="back-btn"
          :icon="ArrowLeft"
          circle
          text
          @click="handleBack"
        />
      </div>

      <!-- 타이틀 -->
      <div class="header-center">
        <h1 class="header-title">{{ title }}</h1>
      </div>

      <!-- 우측 액션 -->
      <div class="header-right">
        <slot name="action"></slot>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'

const props = defineProps({
  title: {
    type: String,
    default: 'Food Finder',
  },
  showBack: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['back'])
const router = useRouter()

const handleBack = () => {
  emit('back')
  router.back()
}
</script>

<style scoped>
.top-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 56px;
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  z-index: 1000;
  padding-top: env(safe-area-inset-top, 0);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 16px;
}

.header-left,
.header-right {
  width: 48px;
  display: flex;
  align-items: center;
}

.header-left {
  justify-content: flex-start;
}

.header-right {
  justify-content: flex-end;
}

.header-center {
  flex: 1;
  text-align: center;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.back-btn {
  font-size: 20px;
}
</style>
