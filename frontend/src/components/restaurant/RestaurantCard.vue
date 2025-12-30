<template>
  <div class="restaurant-card" @click="handleClick">
    <div class="card-content">
      <!-- 음식점 정보 -->
      <div class="restaurant-info">
        <h3 class="restaurant-name">{{ restaurant.name }}</h3>
        <p class="restaurant-category">{{ restaurant.category }}</p>
        
        <div class="info-details">
          <div class="info-item">
            <el-icon><Location /></el-icon>
            <span>{{ displayAddress }}</span>
          </div>
          <div v-if="restaurant.phone" class="info-item">
            <el-icon><Phone /></el-icon>
            <span>{{ restaurant.phone }}</span>
          </div>
        </div>
      </div>

      <!-- 거리 표시 -->
      <div class="restaurant-distance">
        <span class="distance-value">{{ formattedDistance }}</span>
      </div>
    </div>

    <!-- 하단 액션 -->
    <div class="card-actions">
      <el-button
        v-if="restaurant.placeUrl"
        size="small"
        text
        @click.stop="handleOpenKakao"
      >
        <el-icon><Link /></el-icon>
        카카오맵
      </el-button>
      <el-button
        v-if="restaurant.phone"
        size="small"
        text
        @click.stop="handleCall"
      >
        <el-icon><Phone /></el-icon>
        전화
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { Location, Phone, Link } from '@element-plus/icons-vue'

const props = defineProps({
  restaurant: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['click'])
const router = useRouter()

// 주소 표시 (도로명 주소 우선)
const displayAddress = computed(() => {
  return props.restaurant.roadAddress || props.restaurant.address || '주소 정보 없음'
})

// 거리 포맷팅
const formattedDistance = computed(() => {
  const distance = props.restaurant.distance
  if (!distance) return ''
  if (distance < 1000) {
    return `${Math.round(distance)}m`
  }
  return `${(distance / 1000).toFixed(1)}km`
})

// 카드 클릭
const handleClick = () => {
  emit('click', props.restaurant)
  router.push(`/restaurant/${props.restaurant.id}`)
}

// 카카오맵 열기
const handleOpenKakao = () => {
  if (props.restaurant.placeUrl) {
    window.open(props.restaurant.placeUrl, '_blank')
  }
}

// 전화 걸기
const handleCall = () => {
  if (props.restaurant.phone) {
    window.location.href = `tel:${props.restaurant.phone}`
  }
}
</script>

<style scoped>
.restaurant-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.restaurant-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

/* 음식점 정보 */
.restaurant-info {
  flex: 1;
}

.restaurant-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.restaurant-category {
  font-size: 13px;
  color: #666;
  margin: 0 0 8px 0;
}

.info-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

.info-item .el-icon {
  font-size: 14px;
}

/* 거리 */
.restaurant-distance {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.distance-value {
  font-size: 14px;
  font-weight: 600;
  color: #409eff;
}

/* 액션 버튼 */
.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}
</style>
