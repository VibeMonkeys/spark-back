-- ===============================================
-- SPARK 리워드 데이터 설정 스크립트
-- ===============================================
-- 실행 방법: psql -h localhost -p 5432 -U root -d spark -f init_data.sql
-- Railway 실행: Railway CLI 또는 Railway 대시보드에서 실행
-- 
-- 주의: 
-- - 미션 데이터: quick_missions.sql
-- - 사용자 데이터: demo_users.sql
-- - 리워드 데이터: init_data.sql (현재 파일)

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

-- 카페 카테고리 (저가) - 데모용 상품
('프리미엄 아메리카노', '고급 원두로 만든 아메리카노 [데모상품]', 'COFFEE', '카페샘플', '4,500원 상당', 150, 0, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a?w=400', 30, true, true, false, 0, 0, NOW(), NOW()),
('아이스 아메리카노', '시원한 아이스 아메리카노 [데모상품]', 'COFFEE', '카페샘플', '3,500원 상당', 100, 0, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a?w=400', 30, true, false, false, 0, 0, NOW(), NOW()),
('카페라떼', '부드러운 카페라떼 [데모상품]', 'COFFEE', '카페샘플', '5,000원 상당', 180, 0, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a?w=400', 30, true, false, false, 0, 0, NOW(), NOW()),

-- 음식 카테고리 (중가) - 데모용 상품
('배달음식 할인쿠폰', '다양한 음식 주문시 사용 가능한 쿠폰 [데모상품]', 'FOOD', '푸드샘플', '5,000원 상당', 200, 0, 'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400', 30, true, true, false, 0, 0, NOW(), NOW()),
('햄버거 세트', '맛있는 햄버거 세트 [데모상품]', 'FOOD', '푸드샘플', '7,000원 상당', 280, 0, 'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400', 30, true, false, false, 0, 0, NOW(), NOW()),
('도넛 6개 세트', '달콤한 도넛 6개 세트 [데모상품]', 'FOOD', '푸드샘플', '12,000원 상당', 450, 0, 'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400', 30, true, false, false, 0, 0, NOW(), NOW()),

-- 문화/엔터테인먼트 카테고리 (중고가) - 데모용 상품
('영화관람권 A', '최신 영화 관람권 [데모상품]', 'ENTERTAINMENT', '시네마샘플', '13,000원 상당', 500, 0, 'https://images.unsplash.com/photo-1489599135558-1f1d2a21e4a4?w=400', 60, true, true, true, 0, 0, NOW(), NOW()),
('영화관람권 B', '인기 영화 관람권 [데모상품]', 'ENTERTAINMENT', '시네마샘플', '13,000원 상당', 500, 0, 'https://images.unsplash.com/photo-1489599135558-1f1d2a21e4a4?w=400', 60, true, false, true, 0, 0, NOW(), NOW()),
('음악 스트리밍 1개월', '음악 무제한 스트리밍 서비스 [데모상품]', 'ENTERTAINMENT', '뮤직샘플', '10,900원 상당', 420, 0, 'https://images.unsplash.com/photo-1489599135558-1f1d2a21e4a4?w=400', 90, true, false, false, 0, 0, NOW(), NOW()),

-- 책 카테고리 (중가) - 데모용 상품
('온라인 서점 상품권 A', '도서 구매에 사용 가능한 상품권 [데모상품]', 'BOOKS', '북샘플', '10,000원 상당', 400, 0, 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400', 90, true, false, false, 0, 0, NOW(), NOW()),
('온라인 서점 상품권 B', '전자책 및 도서 구매 상품권 [데모상품]', 'BOOKS', '북샘플', '5,000원 상당', 200, 0, 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400', 90, true, true, false, 0, 0, NOW(), NOW()),
('중고서점 할인쿠폰', '중고도서 할인쿠폰 [데모상품]', 'BOOKS', '북샘플', '5,000원 상당', 180, 0, 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400', 90, true, false, false, 0, 0, NOW(), NOW()),

-- 건강 카테고리 (중고가) - 데모용 상품
('프로틴바 세트', '고단백 프로틴바 5개 세트 [데모상품]', 'HEALTH', '헬스샘플', '15,000원 상당', 550, 0, 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400', 30, true, false, false, 0, 0, NOW(), NOW()),
('헬스케어 상품권', '건강식품 전용 상품권 [데모상품]', 'HEALTH', '헬스샘플', '20,000원 상당', 750, 0, 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400', 60, true, true, true, 0, 0, NOW(), NOW()),
('비타민 1개월분', '종합비타민 1개월 분량 [데모상품]', 'HEALTH', '헬스샘플', '25,000원 상당', 900, 0, 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400', 180, true, false, true, 0, 0, NOW(), NOW()),

-- 체험 카테고리 (고가) - 데모용 상품
('자전거 대여 이용권', '공원 자전거 대여 2시간 [데모상품]', 'EXPERIENCE', '체험샘플', '8,000원 상당', 320, 0, 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=400', 30, true, true, false, 0, 0, NOW(), NOW()),
('테마파크 이용권 A', '대형 테마파크 자유이용권 [데모상품]', 'EXPERIENCE', '체험샘플', '56,000원 상당', 2000, 0, 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=400', 30, true, true, true, 0, 0, NOW(), NOW()),
('테마파크 이용권 B', '인기 테마파크 1일권 [데모상품]', 'EXPERIENCE', '체험샘플', '56,000원 상당', 2000, 0, 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=400', 30, true, false, true, 0, 0, NOW(), NOW()),
('스파 이용권', '힐링 스파 1일 이용권 [데모상품]', 'EXPERIENCE', '체험샘플', '15,000원 상당', 580, 0, 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=400', 30, true, false, false, 0, 0, NOW(), NOW());


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
-- - 19개의 데모용 리워드 상품 (실제 상품 아님!)
--   * 카페: 3개 (100-180 포인트)
--   * 음식: 3개 (200-450 포인트) 
--   * 엔터테인먼트: 3개 (420-500 포인트)
--   * 도서: 3개 (180-400 포인트)
--   * 건강: 3개 (550-900 포인트)
--   * 체험: 4개 (320-2000 포인트)
-- - 3개의 데모 사용자 계정 (다양한 레벨과 포인트)
-- 
-- ⚠️ 중요: 모든 리워드는 데모용 상품입니다!
-- - 실제 브랜드명 제거 및 가상 상품명 사용
-- - [데모상품] 표시로 명확한 구분
-- - 추후 실제 상품 연동 예정
-- 
-- 포인트 구간별 상품 분포:
-- - 100-200: 저가 카페 상품 (쉽게 교환 가능)
-- - 200-500: 중가 음식/엔터테인먼트 (적당한 노력 필요)
-- - 500-1000: 고가 건강/도서 상품 (상당한 노력 필요)
-- - 1000+: 최고가 체험 상품 (장기간 노력 필요)
-- 
-- 미션 데이터는 quick_missions.sql에서 별도 관리됩니다!
-- 실행 후 애플리케이션에서 바로 테스트 가능합니다!