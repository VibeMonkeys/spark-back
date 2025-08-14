# Railway 배포 가이드 - SPARK Backend

## 🚀 환경변수 설정

Railway 대시보드에서 다음 환경변수를 설정해주세요:

### 필수 환경변수

| 변수명 | 설명 | 예시 값 |
|--------|------|---------|
| `CORS_ALLOWED_ORIGINS` | CORS에서 허용할 프론트엔드 도메인들 (콤마로 구분) | `http://localhost:5173,https://spark-front-gamma.vercel.app` |
| `PORT` | 서버 포트 (Railway에서 자동 설정) | `8080` |
| `SPRING_PROFILES_ACTIVE` | Spring 프로필 | `production` |

### Railway 환경변수 설정 방법

1. **Railway 대시보드 접속**
   - https://railway.app/dashboard 접속
   - 프로젝트 선택

2. **Variables 탭에서 환경변수 추가**
   ```bash
   # 현재 Vercel 프론트엔드 도메인을 포함한 CORS 설정
   CORS_ALLOWED_ORIGINS=http://localhost:5173,https://spark-front-gamma.vercel.app
   
   # 프로덕션 프로필 활성화
   SPRING_PROFILES_ACTIVE=production
   ```

## 🔧 CORS 설정 작동 방식

### 1. 환경변수 우선순위
```yaml
# application.yml에서 기본값 설정
cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:3001,http://localhost:3002,http://localhost:5173}
```

### 2. 동적 CORS 설정
- `CorsConfig.kt`에서 환경변수를 읽어와서 동적으로 CORS 설정
- 콤마로 구분된 도메인들을 파싱하여 Spring CORS에 적용
- 모든 API 엔드포인트(`/api/**`)에 자동 적용

### 3. 지원하는 설정
- **Methods**: GET, POST, PUT, DELETE, PATCH, OPTIONS
- **Headers**: 모든 헤더 허용
- **Credentials**: 쿠키 및 인증 정보 허용
- **Max Age**: 1시간 프리플라이트 캐싱

## 📋 배포 체크리스트

- [ ] `CORS_ALLOWED_ORIGINS`에 Vercel 프론트엔드 도메인 추가
- [ ] Railway 자동 배포 확인
- [ ] API 응답 상태 확인
- [ ] 프론트엔드에서 CORS 에러 없음 확인

## 🔍 디버깅

### CORS 설정 확인
서버 시작 시 콘솔에서 다음 로그 확인:
```
🌐 [CORS] Configured allowed origins: http://localhost:5173, https://spark-front-gamma.vercel.app
```

### CORS 에러 발생 시
1. Railway Variables에서 `CORS_ALLOWED_ORIGINS` 확인
2. 프론트엔드 도메인이 정확한지 확인 (https/http 프로토콜 포함)
3. Railway 서비스 재시작 후 재테스트

## 🌐 도메인 관리

### 새 프론트엔드 도메인 추가 방법
```bash
# 기존 도메인에 새 도메인 추가 (콤마로 구분)
CORS_ALLOWED_ORIGINS=http://localhost:5173,https://spark-front-gamma.vercel.app,https://new-domain.com
```

### 환경별 설정 예시
```bash
# 개발 환경
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000

# 스테이징 환경  
CORS_ALLOWED_ORIGINS=http://localhost:5173,https://spark-front-staging.vercel.app

# 프로덕션 환경
CORS_ALLOWED_ORIGINS=https://spark-front-gamma.vercel.app,https://spark.your-domain.com
```

## 💡 장점

✅ **중앙집중화**: 모든 CORS 설정을 한 곳에서 관리  
✅ **동적 설정**: 코드 수정 없이 환경변수로 도메인 변경  
✅ **환경별 관리**: 개발/스테이징/프로덕션 환경별 도메인 설정  
✅ **자동 적용**: 모든 컨트롤러에 자동으로 CORS 정책 적용  
✅ **보안**: 허용된 도메인만 API 접근 가능