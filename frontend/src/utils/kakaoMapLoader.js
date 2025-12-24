export function loadKakaoMap() {
  return new Promise((resolve, reject) => {
    // 이미 초기화된 경우
    if (window.kakao && window.kakao.maps && window.kakao.maps.Map) {
      console.log('카카오맵 이미 로드됨')
      resolve(window.kakao)
      return
    }

    // kakao 객체는 있지만 maps가 초기화 안된 경우
    if (window.kakao && window.kakao.maps) {
      console.log('카카오맵 초기화 중...')
      window.kakao.maps.load(() => {
        console.log('카카오맵 초기화 완료')
        resolve(window.kakao)
      })
      return
    }

    // 스크립트가 아예 없는 경우 (index.html에서 로드 실패)
    console.error('카카오맵 스크립트가 로드되지 않았습니다.')
    reject(new Error('카카오맵 SDK가 로드되지 않았습니다. index.html을 확인해주세요.'))
  })
}
