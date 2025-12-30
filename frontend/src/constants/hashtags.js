/**
 * 해시태그 상수
 * 음식 추천 시 선택할 수 있는 해시태그 목록
 * TODO: 나중에 API로 가져오도록 변경 (HashTag 엔티티와 연동)
 */

// 식사 시간대
export const MEAL_TYPES = [
  { code: 'BREAKFAST', label: '아침' },
  { code: 'LUNCH', label: '점심' },
  { code: 'DINNER', label: '저녁' },
]

// 해시태그 목록
export const HASHTAGS = [
  // 기분/상황
  { code: 'COMFORT', label: '든든하게', category: '기분', mealTypes: ['LUNCH', 'DINNER'] },
  { code: 'LIGHT', label: '가볍게', category: '기분', mealTypes: ['BREAKFAST', 'LUNCH'] },
  { code: 'SPECIAL', label: '특별한 날', category: '기분', mealTypes: ['LUNCH', 'DINNER'] },
  { code: 'QUICK', label: '빠르게', category: '기분', mealTypes: ['BREAKFAST', 'LUNCH', 'DINNER'] },
  { code: 'HEALTHY', label: '건강하게', category: '기분', mealTypes: ['BREAKFAST', 'LUNCH', 'DINNER'] },

  // 음식 종류
  { code: 'KOREAN', label: '한식', category: '종류', mealTypes: ['BREAKFAST', 'LUNCH', 'DINNER'] },
  { code: 'CHINESE', label: '중식', category: '종류', mealTypes: ['LUNCH', 'DINNER'] },
  { code: 'JAPANESE', label: '일식', category: '종류', mealTypes: ['LUNCH', 'DINNER'] },
  { code: 'WESTERN', label: '양식', category: '종류', mealTypes: ['LUNCH', 'DINNER'] },
  { code: 'ASIAN', label: '아시안', category: '종류', mealTypes: ['LUNCH', 'DINNER'] },
  { code: 'SNACK', label: '분식', category: '종류', mealTypes: ['LUNCH', 'DINNER'] },
  { code: 'CAFE', label: '카페', category: '종류', mealTypes: ['BREAKFAST', 'LUNCH'] },

  // 맛
  { code: 'SPICY', label: '매운맛', category: '맛', mealTypes: ['LUNCH', 'DINNER'] },
  { code: 'MILD', label: '순한맛', category: '맛', mealTypes: ['BREAKFAST', 'LUNCH', 'DINNER'] },
  { code: 'SAVORY', label: '고소한맛', category: '맛', mealTypes: ['BREAKFAST', 'LUNCH', 'DINNER'] },
  { code: 'SWEET', label: '달콤한맛', category: '맛', mealTypes: ['BREAKFAST', 'LUNCH'] },

  // 가격대
  { code: 'BUDGET', label: '가성비', category: '가격', mealTypes: ['BREAKFAST', 'LUNCH', 'DINNER'] },
  { code: 'PREMIUM', label: '프리미엄', category: '가격', mealTypes: ['LUNCH', 'DINNER'] },

  // 상황
  { code: 'ALONE', label: '혼밥', category: '상황', mealTypes: ['BREAKFAST', 'LUNCH', 'DINNER'] },
  { code: 'DATE', label: '데이트', category: '상황', mealTypes: ['LUNCH', 'DINNER'] },
  { code: 'GROUP', label: '단체', category: '상황', mealTypes: ['LUNCH', 'DINNER'] },
  { code: 'MEETING', label: '회식', category: '상황', mealTypes: ['DINNER'] },
]

/**
 * 카테고리별로 분류된 해시태그
 */
export const HASHTAGS_BY_CATEGORY = HASHTAGS.reduce((acc, item) => {
  if (!acc[item.category]) {
    acc[item.category] = []
  }
  acc[item.category].push(item)
  return acc
}, {})

/**
 * 특정 식사 시간대에 맞는 해시태그 필터링
 * @param {string} mealType - 'BREAKFAST' | 'LUNCH' | 'DINNER'
 */
export const getHashtagsByMealType = (mealType) => {
  return HASHTAGS.filter((tag) => tag.mealTypes.includes(mealType))
}

/**
 * 코드로 해시태그 찾기
 */
export const getHashtagByCode = (code) => {
  return HASHTAGS.find((item) => item.code === code)
}

export default HASHTAGS
