-- ===============================================
-- SPARK 애플리케이션 리워드 및 사용자 데이터 설정 스크립트
-- ===============================================
-- 실행 방법: psql -h localhost -p 5432 -U root -d spark -f init_data.sql
-- Railway 실행: Railway CLI 또는 Railway 대시보드에서 실행
-- 
-- 주의: 미션 데이터는 quick_missions.sql에서 관리됩니다.
-- 이 파일은 리워드와 테스트 사용자 데이터만 포함합니다.

-- 기존 데이터 정리 (개발용 - 프로덕션에서는 주의!)
-- DELETE FROM user_rewards;
-- DELETE FROM rewards;

-- ===============================================
-- 1. 리워드 데이터 (실제 교환 가능한 상품들)
-- ===============================================

-- 기존 리워드 삭제 (개발시에만 사용)
DELETE FROM rewards WHERE 1=1;

INSERT INTO rewards (
    title, description, category, brand, original_price, required_points, 
    discount_percentage, image_url, expiration_days, is_active, is_popular, 
    is_premium, exchange_count, total_exchanged, created_at, updated_at
) VALUES 

-- 카페 카테고리 (저가)
('스타벅스 아메리카노', '스타벅스 아메리카노 기본 사이즈', 'COFFEE', '스타벅스', '4,500원', 150, 10, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a?w=400', 30, true, true, false, 0, 0, NOW(), NOW()),
('이디야 아이스 아메리카노', '이디야 아이스 아메리카노 레귤러', 'COFFEE', '이디야', '3,000원', 100, 15, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a?w=400', 30, true, false, false, 0, 0, NOW(), NOW()),
('투썸플레이스 카페라떼', '투썸플레이스 카페라떼 레귤러', 'COFFEE', '투썸플레이스', '5,000원', 180, 10, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a?w=400', 30, true, false, false, 0, 0, NOW(), NOW()),

-- 음식 카테고리 (중가)
('배달의민족 5,000원 쿠폰', '배달의민족에서 사용 가능한 할인쿠폰', 'FOOD', '배달의민족', '5,000원', 200, 0, 'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400', 30, true, true, false, 0, 0, NOW(), NOW()),
('맥도날드 빅맥 세트', '맥도날드 빅맥 세트 교환권', 'FOOD', '맥도날드', '7,000원', 280, 0, 'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400', 30, true, false, false, 0, 0, NOW(), NOW()),
('던킨도너츠 6개 세트', '던킨도너츠 도넛 6개 세트', 'FOOD', '던킨도너츠', '12,000원', 450, 10, 'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400', 30, true, false, false, 0, 0, NOW(), NOW()),

-- 문화/엔터테인먼트 카테고리 (중고가)
('CGV 영화관람권', 'CGV에서 사용 가능한 영화 관람권', 'ENTERTAINMENT', 'CGV', '13,000원', 500, 0, 'https://images.unsplash.com/photo-1489599135558-1f1d2a21e4a4?w=400', 60, true, true, true, 0, 0, NOW(), NOW()),
('롯데시네마 영화관람권', '롯데시네마 영화 관람권', 'ENTERTAINMENT', '롯데시네마', '13,000원', 500, 0, 'https://images.unsplash.com/photo-1489599135558-1f1d2a21e4a4?w=400', 60, true, false, true, 0, 0, NOW(), NOW()),
('멜론 1개월 이용권', '멜론 음악 스트리밍 1개월 이용권', 'ENTERTAINMENT', '멜론', '10,900원', 420, 5, 'https://images.unsplash.com/photo-1489599135558-1f1d2a21e4a4?w=400', 90, true, false, false, 0, 0, NOW(), NOW()),

-- 책 카테고리 (중가)
('교보문고 1만원 상품권', '교보문고에서 사용 가능한 도서 상품권', 'BOOKS', '교보문고', '10,000원', 400, 0, 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400', 90, true, false, false, 0, 0, NOW(), NOW()),
('예스24 5천원 상품권', '예스24 온라인 서점 상품권', 'BOOKS', '예스24', '5,000원', 200, 0, 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400', 90, true, true, false, 0, 0, NOW(), NOW()),
('알라딘 중고서점 5천원 쿠폰', '알라딘 중고서점 할인쿠폰', 'BOOKS', '알라딘', '5,000원', 180, 10, 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400', 90, true, false, false, 0, 0, NOW(), NOW()),

-- 건강 카테고리 (중고가)
('GS25 단백질바 세트', 'GS25 프로틴바 5개 세트', 'HEALTH', 'GS25', '15,000원', 550, 10, 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400', 30, true, false, false, 0, 0, NOW(), NOW()),
('이마트 헬스케어 상품권', '이마트 건강식품 전용 상품권', 'HEALTH', '이마트', '20,000원', 750, 5, 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400', 60, true, true, true, 0, 0, NOW(), NOW()),
('닥터유 비타민 1개월분', '닥터유 종합비타민 1개월 분량', 'HEALTH', '닥터유', '25,000원', 900, 10, 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400', 180, true, false, true, 0, 0, NOW(), NOW()),

-- 체험 카테고리 (고가)
('한강 자전거 대여 2시간', '한강공원 자전거 대여 2시간 이용권', 'EXPERIENCE', '서울시설공단', '8,000원', 320, 0, 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=400', 30, true, true, false, 0, 0, NOW(), NOW()),
('롯데월드 자유이용권', '롯데월드 어드벤처 자유이용권', 'EXPERIENCE', '롯데월드', '56,000원', 2000, 10, 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=400', 30, true, true, true, 0, 0, NOW(), NOW()),
('에버랜드 자유이용권', '에버랜드 1일 자유이용권', 'EXPERIENCE', '에버랜드', '56,000원', 2000, 10, 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=400', 30, true, false, true, 0, 0, NOW(), NOW()),
('찜질방 이용권', '드래곤힐스파 찜질방 1일 이용권', 'EXPERIENCE', '드래곤힐스파', '15,000원', 580, 5, 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=400', 30, true, false, false, 0, 0, NOW(), NOW());

-- ===============================================
-- 2. 데모 사용자 계정 (개발/테스트용) - Long ID 시스템에 맞게 수정
-- ===============================================

-- 기존 사용자 정보 업데이트 (Long ID 사용)
INSERT INTO users (
    name, email, password, avatar_url, level, level_title, current_points, total_points,
    current_streak, longest_streak, completed_missions, total_days, preferences,
    this_month_points, this_month_missions, average_rating, total_ratings,
    category_stats, created_at, updated_at, last_login_at
) VALUES 
(
    '테스트유저1',
    'testuser1@spark.com',
    '$2a$10$demopassword.encrypted.hash.for.testing.purposes.only',
    'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face',
    5,
    'EXPLORER', 
    450,
    1200,
    5,
    12,
    18,
    25,
    '{"notifications": true, "email_alerts": true}',
    450,
    8,
    4.5,
    10,
    '{"ADVENTURE": 5, "HEALTH": 4, "CREATIVE": 3, "LEARNING": 3, "SOCIAL": 3}',
    NOW(),
    NOW(),
    NOW()
),
(
    '테스트유저2',
    'testuser2@spark.com',
    '$2a$10$demopassword.encrypted.hash.for.testing.purposes.only',
    'https://images.unsplash.com/photo-1494790108755-2616b9e6e3e7?w=150&h=150&fit=crop&crop=face',
    8,
    'EXPLORER',
    720,
    2500,
    12,
    25,
    35,
    42,
    '{"notifications": true, "email_alerts": false}',
    720,
    15,
    4.2,
    25,
    '{"ADVENTURE": 8, "HEALTH": 7, "CREATIVE": 6, "LEARNING": 8, "SOCIAL": 6}',
    NOW(),
    NOW(),
    NOW()
),
(
    '고급유저',
    'premium@spark.com',
    '$2a$10$demopassword.encrypted.hash.for.testing.purposes.only',
    'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face',
    15,
    'ADVENTURER',
    1800,
    8500,
    25,
    45,
    85,
    120,
    '{"notifications": true, "email_alerts": true}',
    1800,
    35,
    4.8,
    60,
    '{"ADVENTURE": 20, "HEALTH": 18, "CREATIVE": 15, "LEARNING": 17, "SOCIAL": 15}',
    NOW(),
    NOW(),
    NOW()
);

-- ===============================================
-- 데이터 확인 쿼리 (참고용)
-- ===============================================

-- SELECT '=== 리워드 수 ===' as info, COUNT(*) as count FROM rewards
-- UNION ALL
-- SELECT '=== 데모 사용자 수 ===' as info, COUNT(*) as count FROM users;

-- ===============================================
-- 초기 데이터 설정 완료!
-- ===============================================
-- 이 스크립트로 다음이 생성됩니다:
-- - 20개의 실제 교환 가능한 리워드 상품
--   * 카페: 3개 (100-180 포인트)
--   * 음식: 3개 (200-450 포인트) 
--   * 엔터테인먼트: 3개 (420-500 포인트)
--   * 도서: 3개 (180-400 포인트)
--   * 건강: 3개 (550-900 포인트)
--   * 체험: 5개 (320-2000 포인트)
-- - 3개의 데모 사용자 계정 (다양한 레벨과 포인트)
-- 
-- 포인트 구간별 상품 분포:
-- - 100-200: 저가 카페 상품 (쉽게 교환 가능)
-- - 200-500: 중가 음식/엔터테인먼트 (적당한 노력 필요)
-- - 500-1000: 고가 건강/도서 상품 (상당한 노력 필요)
-- - 1000+: 최고가 체험 상품 (장기간 노력 필요)
-- 
-- 미션 데이터는 quick_missions.sql에서 별도 관리됩니다!
-- 실행 후 애플리케이션에서 바로 테스트 가능합니다!