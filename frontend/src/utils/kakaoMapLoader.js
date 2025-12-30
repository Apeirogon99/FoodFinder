/**
 * 카카오맵 SDK 동적 로더
 * 환경변수에서 API 키를 가져와 스크립트를 동적으로 로드합니다.
 */

const KAKAO_MAP_KEY = import.meta.env.VITE_KAKAO_MAP_KEY

let isLoading = false
let loadPromise = null

export function loadKakaoMap() {
  // 이미 로드 완료된 경우
  if (window.kakao && window.kakao.maps && window.kakao.maps.Map) {
    console.log('✅ 카카오맵 이미 로드됨')
    return Promise.resolve(window.kakao)
  }

  // 로드 중인 경우 기존 Promise 반환
  if (isLoading && loadPromise) {
    console.log('⏳ 카카오맵 로드 중...')
    return loadPromise
  }

  // 새로 로드 시작
  isLoading = true
  loadPromise = new Promise((resolve, reject) => {
    // API 키 확인
    if (!KAKAO_MAP_KEY) {
      reject(new Error('카카오맵 API 키가 설정되지 않았습니다. .env 파일을 확인해주세요.'))
      return
    }

    // 이미 스크립트가 로드되어 있지만 초기화가 안된 경우
    if (window.kakao && window.kakao.maps) {
      console.log('🔄 카카오맵 초기화 중...')
      window.kakao.maps.load(() => {
        console.log('✅ 카카오맵 초기화 완료')
        isLoading = false
        resolve(window.kakao)
      })
      return
    }

    // 스크립트 동적 로드
    console.log('📥 카카오맵 스크립트 로드 시작...')
    
    const script = document.createElement('script')
    script.type = 'text/javascript'
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${KAKAO_MAP_KEY}&autoload=false&libraries=services,clusterer`
    
    script.onload = () => {
      console.log('📦 카카오맵 스크립트 로드 완료, 초기화 시작...')
      
      if (window.kakao && window.kakao.maps) {
        window.kakao.maps.load(() => {
          console.log('✅ 카카오맵 초기화 완료')
          isLoading = false
          resolve(window.kakao)
        })
      } else {
        isLoading = false
        reject(new Error('카카오맵 SDK 로드 후 초기화 실패'))
      }
    }
    
    script.onerror = () => {
      console.error('❌ 카카오맵 스크립트 로드 실패')
      isLoading = false
      reject(new Error('카카오맵 스크립트 로드에 실패했습니다. 네트워크를 확인해주세요.'))
    }
    
    document.head.appendChild(script)
  })

  return loadPromise
}

export default loadKakaoMap
