<template>
  <div class="app-layout">
    <!-- 헤더 -->
    <TopHeader :title="title" :show-back="showBack" @back="$emit('back')">
      <template #action>
        <slot name="header-action"></slot>
      </template>
    </TopHeader>

    <!-- 메인 컨텐츠 영역 -->
    <main class="app-content" :class="{ 
      'no-bottom-nav': hideBottomNav,
      'has-footer': hasFooter 
    }">
      <slot></slot>
    </main>

    <!-- 커스텀 Footer 영역 (BottomNav 위) -->
    <div v-if="hasFooter" class="app-footer">
      <slot name="footer"></slot>
    </div>

    <!-- 하단 네비게이션 -->
    <BottomNav v-if="!hideBottomNav" />
  </div>
</template>

<script setup>
import { useSlots, computed } from 'vue'
import TopHeader from './TopHeader.vue'
import BottomNav from './BottomNav.vue'

defineProps({
  title: {
    type: String,
    default: 'Food Finder',
  },
  showBack: {
    type: Boolean,
    default: false,
  },
  hideBottomNav: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['back'])

const slots = useSlots()
const hasFooter = computed(() => !!slots.footer)
</script>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f5;
  max-width: 430px;
  margin: 0 auto;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

/* 메인 컨텐츠 */
.app-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding-top: 56px; /* TopHeader 높이 */
  padding-bottom: 64px; /* BottomNav 높이 */
  overflow-y: auto;
}

.app-content.no-bottom-nav {
  padding-bottom: 0;
}

.app-content.has-footer {
  padding-bottom: 0;
}

/* 커스텀 Footer */
.app-footer {
  background: white;
  border-top: 1px solid #eee;
  margin-bottom: 64px; /* BottomNav 높이 */
}
</style>
