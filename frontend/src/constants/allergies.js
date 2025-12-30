/**
 * 알레르기 타입 상수
 * 백엔드 AllergyType enum과 동기화
 * TODO: 나중에 API로 가져오도록 변경
 */
export const ALLERGY_TYPES = [
  // 주요 식품 알레르기
  { code: 'PEANUT', group: '알레르기', label: '땅콩', icon: '🥜' },
  { code: 'TREE_NUT', group: '알레르기', label: '견과류', icon: '🌰' },
  { code: 'MILK', group: '알레르기', label: '우유', icon: '🥛' },
  { code: 'EGG', group: '알레르기', label: '계란', icon: '🥚' },
  { code: 'WHEAT', group: '알레르기', label: '밀', icon: '🌾' },
  { code: 'SOY', group: '알레르기', label: '대두', icon: '🫘' },
  { code: 'SHRIMP', group: '알레르기', label: '새우', icon: '🦐' },
  { code: 'CRAB', group: '알레르기', label: '게', icon: '🦀' },
  { code: 'SHELLFISH', group: '알레르기', label: '조개류', icon: '🐚' },
  { code: 'FISH', group: '알레르기', label: '생선', icon: '🐟' },
  { code: 'PORK', group: '알레르기', label: '돼지고기', icon: '🐷' },
  { code: 'CHICKEN', group: '알레르기', label: '닭고기', icon: '🐔' },
  { code: 'BEEF', group: '알레르기', label: '소고기', icon: '🐄' },

  // 식이 제한
  { code: 'VEGAN', group: '식이제한', label: '비건', icon: '🥬' },
  { code: 'LACTOSE_FREE', group: '식이제한', label: '락토-프리', icon: '🚫' },
  { code: 'GLUTEN_FREE', group: '식이제한', label: '글루텐-프리', icon: '🌿' },
]

// ALLERGIES alias for easier import
export const ALLERGIES = ALLERGY_TYPES

/**
 * 그룹별로 분류된 알레르기 타입
 */
export const ALLERGY_TYPES_BY_GROUP = ALLERGY_TYPES.reduce((acc, item) => {
  if (!acc[item.group]) {
    acc[item.group] = []
  }
  acc[item.group].push(item)
  return acc
}, {})

/**
 * 코드로 알레르기 타입 찾기
 */
export const getAllergyByCode = (code) => {
  return ALLERGY_TYPES.find((item) => item.code === code)
}

export default ALLERGY_TYPES
