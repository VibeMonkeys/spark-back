# SPARK 초기 데이터 설정 가이드

## 📋 개요

`init_data.sql` 파일은 SPARK 애플리케이션의 기본 데이터를 한 번에 설정하는 통합 스크립트입니다.

## 🗃️ 생성되는 데이터

### 1. 템플릿 미션 (10개)
- **모험적 미션**: 새로운 동네 탐험, 모르는 사람과 대화하기
- **사교적 미션**: 프리스비 함께 하기, 소상공인에게 감사 인사
- **건강 미션**: 15분 산책하기, 계단 10층 올라가기  
- **창의적 미션**: 하늘 그리기, 일상 사물 예술 작품 만들기
- **학습 미션**: 새로운 단어 5개 배우기, 요리 레시피 배우기

### 2. 플레이스홀더 리워드 (6개)
- 카페 상품 (스타벅스, 이디야)
- 배달음식 쿠폰
- 영화 관람권
- 온라인 쇼핑 쿠폰
- 편의점 상품권
- *모든 리워드는 99,999 포인트로 설정 (구매 불가)*

### 3. 데모 사용자 계정 (2개)
- **김철수** (`user_01`): 레벨 3 탐험가 (1,250 포인트, 25개 미션 완료)
- **지나니** (`2190d61c-379d-4452-b4da-655bf67b4b71`): 레벨 1 초보자 (320 포인트, 8개 미션 완료)
- 프론트엔드에서 데모 계정으로 바로 로그인 가능
- 실제 사용자 데이터 구조와 동일한 형태로 구성

## 🚀 실행 방법

### 로컬 환경 (PostgreSQL)
```bash
# PostgreSQL 서버 시작
docker-compose up -d postgres

# 스크립트 실행
PGPASSWORD=1234 psql -h localhost -p 5432 -U root -d spark -f src/main/resources/init_data.sql
```

### Railway 환경
1. **Railway 대시보드** 접속
2. **Database 탭** 선택
3. **Query** 섹션에서 `init_data.sql` 파일 내용 복사/붙여넣기
4. **Execute** 버튼 클릭

### Railway CLI 사용
```bash
# Railway CLI 설치 및 로그인 후
railway run psql -f src/main/resources/init_data.sql
```

## ✅ 실행 후 확인

### 데이터 확인 쿼리
```sql
-- 생성된 데이터 확인
SELECT '템플릿 미션' as 구분, COUNT(*) as 개수 FROM missions WHERE is_template = true
UNION ALL
SELECT '리워드' as 구분, COUNT(*) as 개수 FROM rewards;
```

### 기대 결과
```
구분       | 개수
-----------|-----
템플릿 미션  | 10
리워드      | 6+ (기존 데이터 포함)
데모 사용자  | 2
```

## 🧪 애플리케이션 테스트

### 데모 계정으로 테스트
1. **로그인 화면**: "데모 계정으로 체험하기" 클릭
2. **계정 선택**: 김철수(고급 사용자) 또는 지나니(초보 사용자) 선택
3. **바로 로그인**: 회원가입 없이 즉시 앱 체험 가능

### 기능별 테스트
1. **홈페이지**: 템플릿 미션 기반 추천 미션 표시
2. **탐색페이지**: 사용자들의 스토리가 누적되면 피드 표시  
3. **미션페이지**: 할당받은 미션 및 완료 미션 표시
4. **리워드페이지**: 6개 플레이스홀더 상품 표시
5. **프로필페이지**: 데모 사용자 정보 및 통계 표시

## ⚠️ 주의사항

### 데이터 초기화 (주의!)
스크립트 상단의 DELETE 문들은 주석 처리되어 있습니다. 
필요 시 주석을 해제하여 기존 데이터를 삭제할 수 있습니다:

```sql
-- 주석 해제 시 기존 데이터 삭제됨 (개발용)
DELETE FROM story_likes;
DELETE FROM story_comments;  
DELETE FROM stories;
DELETE FROM user_rewards;
DELETE FROM rewards;
DELETE FROM missions WHERE is_template = false;
DELETE FROM missions WHERE is_template = true;
```

### 프로덕션 환경
- 프로덕션에서는 DELETE 문 사용 금지
- 사용자는 실제 회원가입으로만 생성
- 리워드는 실제 상품으로 교체 필요

## 🔧 커스터마이징

### 미션 추가
```sql
INSERT INTO missions (
    id, user_id, title, description, detailed_description, 
    category, difficulty, status, reward_points, estimated_minutes, 
    is_template, conditions, tips, location, weather_conditions, 
    available_time_slots, image_url, created_at, updated_at
) VALUES (
    'tpl_custom_001', '', '새로운 미션 제목', 
    '간단한 설명', '상세한 설명',
    'ADVENTURE', 'EASY', 'ASSIGNED', 20, 30, true,
    '조건들', '팁들', '장소', '날씨', '시간대',
    'https://example.com/image.jpg', NOW(), NOW()
);
```

### 리워드 추가  
```sql
INSERT INTO rewards (
    id, title, description, category, brand, original_price, 
    required_points, discount_percentage, image_url, 
    expiration_days, is_active, is_popular, is_premium,
    exchange_count, total_exchanged, created_at, updated_at
) VALUES (
    'custom_reward_01', '상품명', '상품 설명', 'COFFEE', 
    '브랜드명', '가격', 1000, 0, 'https://example.com/image.jpg',
    30, true, false, false, 0, 0, NOW(), NOW()
);
```

## 💡 팁

1. **순차 실행**: 스크립트는 FK 관계를 고려해 순서대로 작성됨
2. **충돌 방지**: `ON CONFLICT DO NOTHING` 구문으로 중복 실행 방지
3. **시간 설정**: `NOW()` 함수로 현재 시간 자동 설정
4. **이미지 URL**: Unsplash 무료 이미지 사용
5. **현실적 데이터**: 실제 사용 가능한 미션 및 스토리 내용

실행 후 애플리케이션의 모든 기능을 바로 테스트할 수 있습니다! 🎉