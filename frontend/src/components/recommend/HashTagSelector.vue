<template>
  <div class="hashtag-selector">
    <!-- 식사 시간대 선택 -->
    <div class="meal-type-section">
      <h4 class="section-label">🕐 식사 시간</h4>
      <div class="meal-types">
        <el-radio-group v-model="selectedMealType" size="large">
          <el-radio-button
            v-for="meal in MEAL_TYPES"
            :key="meal.code"
            :value="meal.code"
          >
            {{ meal.label }}
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 해시태그 선택 -->
    <div class="hashtag-section">
      <div
        v-for="(tags, category) in filteredHashtagsByCategory"
        :key="category"
        class="category-group"
      >
        <h4 class="section-label">
          {{ categoryIcons[category] }} {{ category }}
        </h4>
        <div class="hashtag-tags">
          <el-tag
            v-for="tag in tags"
            :key="tag.code"
            :type="isSelected(tag.code) ? '' : 'info'"
            :effect="isSelected(tag.code) ? 'dark' : 'plain'"
            class="hashtag-tag"
            size="large"
            @click="toggleHashtag(tag.code)"
          >
            {{ tag.label }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 선택된 태그 표시 -->
    <div v-if="selectedTags.length > 0" class="selected-section">
      <div class="selected-header">
        <span class="selected-label">선택된 태그 ({{ selectedTags.length }})</span>
        <el-button type="text" size="small" @click="clearSelection">
          전체 해제
        </el-button>
      </div>
      <div class="selected-tags">
        <el-tag
          v-for="code in selectedTags"
          :key="code"
          closable
          effect="dark"
          @close="toggleHashtag(code)"
        >
          {{ getTagLabel(code) }}
        </el-tag>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { 
  MEAL_TYPES, 
  HASHTAGS, 
  getHashtagsByMealType,
  getHashtagByCode 
} from '@/constants/hashtags'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => [],
  },
  mealType: {
    type: String,
    default: null,
  },
})

const emit = defineEmits(['update:modelValue', 'update:mealType'])

// 현재 시간 기반으로 기본 식사 시간 설정
const getDefaultMealType = () => {
  const hour = new Date().getHours()
  if (hour >= 5 && hour < 10) return 'BREAKFAST'
  if (hour >= 10 && hour < 15) return 'LUNCH'
  return 'DINNER'
}

// 선택된 식사 시간대
const selectedMealType = ref(props.mealType || getDefaultMealType())

// 선택된 태그들
const selectedTags = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})

// 카테고리별 아이콘
const categoryIcons = {
  '기분': '😊',
  '종류': '🍽️',
  '맛': '👅',
  '가격': '💰',
  '상황': '👥',
}

// 필터링된 해시태그 (식사 시간대 기준)
const filteredHashtagsByCategory = computed(() => {
  const filtered = getHashtagsByMealType(selectedMealType.value)
  return filtered.reduce((acc, item) => {
    if (!acc[item.category]) {
      acc[item.category] = []
    }
    acc[item.category].push(item)
    return acc
  }, {})
})

// 태그 선택 여부
const isSelected = (code) => {
  return selectedTags.value.includes(code)
}

// 태그 토글
const toggleHashtag = (code) => {
  const newTags = [...selectedTags.value]
  const index = newTags.indexOf(code)
  
  if (index === -1) {
    newTags.push(code)
  } else {
    newTags.splice(index, 1)
  }
  
  selectedTags.value = newTags
}

// 태그 라벨 가져오기
const getTagLabel = (code) => {
  const tag = getHashtagByCode(code)
  return tag ? tag.label : code
}

// 선택 초기화
const clearSelection = () => {
  selectedTags.value = []
}

// 식사 시간대 변경 시 emit
watch(selectedMealType, (newValue) => {
  emit('update:mealType', newValue)
  
  // 새 시간대에 맞지 않는 태그 제거
  const validCodes = getHashtagsByMealType(newValue).map(t => t.code)
  const filtered = selectedTags.value.filter(code => validCodes.includes(code))
  if (filtered.length !== selectedTags.value.length) {
    selectedTags.value = filtered
  }
})
</script>

<style scoped>
.hashtag-selector {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 섹션 스타일 */
.meal-type-section,
.hashtag-section,
.selected-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.section-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

/* 식사 시간대 */
.meal-types {
  display: flex;
  justify-content: center;
}

.meal-types :deep(.el-radio-button__inner) {
  padding: 12px 24px;
}

/* 해시태그 카테고리 */
.category-group {
  margin-bottom: 8px;
}

.hashtag-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.hashtag-tag {
  cursor: pointer;
  transition: all 0.2s;
  padding: 8px 16px;
  font-size: 13px;
}

.hashtag-tag:hover {
  transform: scale(1.05);
}

/* 선택된 태그 */
.selected-section {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 12px;
}

.selected-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selected-label {
  font-size: 13px;
  font-weight: 500;
  color: #666;
}

.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}
</style>
