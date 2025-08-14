-- ===============================================
-- SPARK 애플리케이션 초기 데이터 설정 스크립트
-- ===============================================
-- 실행 방법: psql -h localhost -p 5432 -U root -d spark -f init_data.sql
-- Railway 실행: Railway CLI 또는 Railway 대시보드에서 실행

-- 기존 데이터 정리 (개발용 - 프로덕션에서는 주의!)
-- DELETE FROM story_likes;
-- DELETE FROM story_comments;
-- DELETE FROM stories;
-- DELETE FROM user_rewards;
-- DELETE FROM rewards;
-- DELETE FROM missions WHERE is_template = false;
-- DELETE FROM missions WHERE is_template = true;

-- ===============================================
-- 1. 템플릿 미션 데이터 (is_template = true)
-- ===============================================

INSERT INTO missions (
    id, user_id, title, description, detailed_description, category, difficulty, status,
    reward_points, estimated_minutes, is_template, conditions, tips, location,
    weather_conditions, available_time_slots, image_url, assigned_at, expires_at, created_at, updated_at,
    progress, completed_by, average_rating, total_ratings, average_completion_time, popularity_score
) VALUES 
-- 모험적 미션들
(
    'tpl_adv_001', '', '새로운 동네 탐험하기', 
    '평소 가보지 않은 동네를 걸어보고 새로운 장소를 발견해보세요.',
    '집이나 직장 근처에서 평소 가보지 않은 길로 20분 이상 걸어보세요. 새로운 카페, 공원, 상점 등을 발견하고 사진을 찍어 인증해주세요. 단순히 길을 걷는 것이 아니라 새로운 것을 발견하는 것이 목표입니다.',
    'ADVENTURE', 'EASY', 'ASSIGNED',
    20, 30, true, '20분 이상 걸어야 함,사진 인증 필요', '새로운 길을 선택하세요,호기심을 가지고 둘러보세요', 
    '야외', '맑음,흐림', '06:00-22:00', 
    'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400',
    NOW(), NOW() + INTERVAL '30 days', NOW(), NOW(),
    0, 0, 0.0, 0, 0, 0.0
),
(
    'tpl_adv_002', '', '모르는 사람과 대화하기',
    '카페나 공원에서 모르는 사람과 자연스럽게 5분 이상 대화해보세요.',
    '공공장소에서 모르는 사람과 자연스럽게 대화를 시작해보세요. 날씨, 반려동물, 책 등 가벼운 주제로 시작할 수 있습니다. 상대방이 불편해하지 않는 선에서 진행하며, 대화 내용을 간단히 기록해주세요.',
    'ADVENTURE', 'MEDIUM', 'ASSIGNED',
    30, 20, true, '5분 이상 대화,공공장소에서 진행', '자연스러운 주제로 시작하세요,상대방을 배려하세요',
    '카페,공원,도서관', '상관없음', '10:00-18:00',
    'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400',
    NOW(), NOW() + INTERVAL '30 days', NOW(), NOW(),
    0, 0, 0.0, 0, 0, 0.0
),

-- 사교적 미션들
(
    'tpl_soc_001', '', '카페에서 낯선 사람과 프리스비 하기',
    '한강이나 공원에서 혼자 프리스비를 던지다 자연스럽게 함께할 사람을 찾아보세요.',
    '프리스비나 공을 가지고 공원에 가서 혼자 던지기 시작하세요. 지나가는 사람들이 관심을 보이면 함께 하자고 제안해보세요. 15분 이상 함께 활동하고 새로운 인연을 만들어보세요.',
    'SOCIAL', 'MEDIUM', 'ASSIGNED',
    25, 40, true, '15분 이상 함께 활동,야외에서 진행', '프리스비나 공을 준비하세요,개방적인 마음가짐으로 시작하세요',
    '한강공원,올림픽공원', '맑음', '14:00-18:00',
    'https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=400',
    NOW(), NOW() + INTERVAL '30 days', NOW(), NOW(),
    0, 0, 0.0, 0, 0, 0.0
),
(
    'tpl_soc_002', '', '동네 소상공인에게 감사 인사하기',
    '평소 이용하는 가게 사장님께 감사 인사를 전해보세요.',
    '동네에서 자주 이용하는 가게(카페, 식당, 편의점 등)의 사장님이나 직원분께 평소 감사했던 마음을 진심으로 전해보세요. 간단한 편지나 직접 대화를 통해 따뜻한 마음을 나누어보세요.',
    'SOCIAL', 'EASY', 'ASSIGNED',
    15, 15, true, '직접 전달,진심어린 감사 표현', '진심을 담아 표현하세요,부담스럽지 않게 간단히 해보세요',
    '동네 가게', '상관없음', '10:00-20:00',
    'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400',
    NOW(), NOW() + INTERVAL '30 days', NOW(), NOW(),
    0, 0, 0.0, 0, 0, 0.0
),

-- 건강 미션들
(
    'tpl_hlt_001', '', '15분 산책하기',
    '동네를 15분간 여유롭게 산책하며 자연을 감상해보세요.',
    '스마트폰을 잠시 내려놓고 동네를 15분간 천천히 산책해보세요. 주변의 나무, 꽃, 건물 등을 자세히 관찰하며 평소 지나쳤던 것들을 새롭게 발견해보세요. 산책 후 느낀 점을 간단히 기록해주세요.',
    'HEALTH', 'EASY', 'ASSIGNED',
    10, 15, true, '15분 연속 걷기,스마트폰 사용 자제', '천천히 걸으세요,주변을 관찰하며 걸어보세요',
    '동네,공원', '상관없음', '06:00-22:00',
    'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400',
    NOW(), NOW() + INTERVAL '30 days', NOW(), NOW(),
    0, 0, 0.0, 0, 0, 0.0
),
(
    'tpl_hlt_002', '', '계단으로 10층 올라가기',
    '엘리베이터 대신 계단을 이용해 10층까지 올라가보세요.',
    '아파트, 회사, 상가 등에서 엘리베이터 대신 계단을 이용해 10층 이상 올라가보세요. 무리하지 말고 자신의 페이스에 맞춰 진행하며, 중간에 쉬어가도 괜찮습니다. 완주 후 성취감을 기록해주세요.',
    'HEALTH', 'MEDIUM', 'ASSIGNED',
    20, 10, true, '10층 이상 올라가기,안전 주의', '무리하지 마세요,중간에 쉬어가도 OK',
    '아파트,회사,상가', '상관없음', '상관없음',
    'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400',
    NOW(), NOW() + INTERVAL '30 days', NOW(), NOW(),
    0, 0, 0.0, 0, 0, 0.0
),

-- 창의적 미션들
(
    'tpl_cre_001', '', '오늘의 하늘 그리기',
    '오늘 하늘을 보고 느낀 감정을 그림으로 표현해보세요.',
    '창 밖을 보거나 밖으로 나가서 하늘을 관찰해보세요. 구름의 모양, 색깔, 느낌을 자신만의 방식으로 그려보세요. 실력은 중요하지 않습니다. 오늘의 기분과 하늘의 모습을 자유롭게 표현해보세요.',
    'CREATIVE', 'EASY', 'ASSIGNED',
    15, 20, true, '직접 그리기,하늘 관찰 후 진행', '실력보다는 감정 표현이 중요해요,자유롭게 그려보세요',
    '집,야외', '상관없음', '상관없음',
    'https://images.unsplash.com/photo-1541961017774-22349e4a1262?w=400',
    NOW(), NOW() + INTERVAL '30 days', NOW(), NOW(),
    0, 0, 0.0, 0, 0, 0.0
),
(
    'tpl_cre_002', '', '일상 사물로 예술 작품 만들기',
    '집에 있는 일상 사물들을 이용해 작은 예술 작품을 만들어보세요.',
    '집에 있는 일상적인 물건들(펜, 책, 컵, 과일 등)을 이용해 창의적인 배치나 구성을 만들어보세요. 색깔, 모양, 질감을 고려해 나만의 작은 설치 예술을 만들고 사진으로 기록해주세요.',
    'CREATIVE', 'EASY', 'ASSIGNED',
    20, 25, true, '기존 사물 활용,창의적 배치', '완벽함보다는 창의성이 중요해요,즐기면서 해보세요',
    '집', '상관없음', '상관없음',
    'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=400',
    NOW(), NOW() + INTERVAL '30 days', NOW(), NOW(),
    0, 0, 0.0, 0, 0, 0.0
),

-- 학습 미션들
(
    'tpl_lea_001', '', '새로운 단어 5개 배우기',
    '오늘 새로운 한국어/영어 단어 5개를 배우고 문장으로 만들어보세요.',
    '사전, 책, 인터넷을 통해 모르는 단어 5개를 찾아보세요. 각 단어의 뜻을 정확히 이해하고, 실제 사용할 수 있는 문장을 만들어보세요. 단순 암기가 아닌 실용적 학습이 목표입니다.',
    'LEARNING', 'EASY', 'ASSIGNED',
    15, 20, true, '5개 단어 학습,문장 만들기', '실생활에서 쓸 수 있는 단어를 선택하세요,문맥과 함께 기억하세요',
    '집,도서관,카페', '상관없음', '상관없음',
    'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400',
    NOW(), NOW() + INTERVAL '30 days', NOW(), NOW(),
    0, 0, 0.0, 0, 0, 0.0
),
(
    'tpl_lea_002', '', '새로운 요리 레시피 배우기',
    '유튜브나 책을 보고 새로운 요리를 직접 만들어보세요.',
    '평소 만들어보지 않은 새로운 요리 레시피를 찾아보고 직접 요리해보세요. 간단한 요리도 좋고, 도전적인 요리도 좋습니다. 과정을 사진으로 기록하고 맛이나 느낀 점을 공유해주세요.',
    'LEARNING', 'MEDIUM', 'ASSIGNED',
    25, 45, true, '새로운 레시피,직접 요리하기', '실패해도 괜찮아요,과정을 즐기세요',
    '집', '상관없음', '상관없음',
    'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400',
    NOW(), NOW() + INTERVAL '30 days', NOW(), NOW(),
    0, 0, 0.0, 0, 0, 0.0
)
ON CONFLICT (id) DO UPDATE SET
    title = EXCLUDED.title,
    description = EXCLUDED.description,
    detailed_description = EXCLUDED.detailed_description,
    category = EXCLUDED.category,
    difficulty = EXCLUDED.difficulty,
    reward_points = EXCLUDED.reward_points,
    estimated_minutes = EXCLUDED.estimated_minutes,
    conditions = EXCLUDED.conditions,
    tips = EXCLUDED.tips,
    location = EXCLUDED.location,
    weather_conditions = EXCLUDED.weather_conditions,
    available_time_slots = EXCLUDED.available_time_slots,
    image_url = EXCLUDED.image_url,
    updated_at = NOW();

-- ===============================================
-- 2. 리워드 데이터 (플레이스홀더)
-- ===============================================

INSERT INTO rewards (
    id, title, description, category, brand, original_price, required_points, 
    discount_percentage, image_url, expiration_days, is_active, is_popular, 
    is_premium, exchange_count, total_exchanged, created_at, updated_at
) VALUES 
-- 카페 카테고리
(
    'reward_coffee_01', 
    '스타벅스 상품 추가 예정', 
    '다양한 스타벅스 메뉴가 곧 추가됩니다', 
    'COFFEE', 
    '추가 예정', 
    '미정',
    99999, 
    0, 
    'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a?w=400', 
    30, 
    true, 
    false, 
    false, 
    0, 
    0, 
    NOW(), 
    NOW()
),
(
    'reward_coffee_02', 
    '이디야 상품 추가 예정', 
    '이디야 메뉴가 곧 추가됩니다', 
    'COFFEE', 
    '추가 예정', 
    '미정',
    99999, 
    0, 
    'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a?w=400', 
    30, 
    true, 
    false, 
    false, 
    0, 
    0, 
    NOW(), 
    NOW()
),

-- 음식 카테고리
(
    'reward_food_01', 
    '배달음식 쿠폰 추가 예정', 
    '다양한 배달음식 할인쿠폰이 곧 추가됩니다', 
    'FOOD', 
    '추가 예정', 
    '미정',
    99999, 
    0, 
    'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400', 
    30, 
    true, 
    true, 
    false, 
    0, 
    0, 
    NOW(), 
    NOW()
),

-- 문화 카테고리
(
    'reward_culture_01', 
    '영화 관람권 추가 예정', 
    '영화관 이용권이 곧 추가됩니다', 
    'ENTERTAINMENT', 
    '추가 예정', 
    '미정',
    99999, 
    0, 
    'https://images.unsplash.com/photo-1489599135558-1f1d2a21e4a4?w=400', 
    30, 
    true, 
    false, 
    true, 
    0, 
    0, 
    NOW(), 
    NOW()
),

-- 쇼핑 카테고리
(
    'reward_shopping_01', 
    '온라인 쇼핑몰 쿠폰 추가 예정', 
    '다양한 쇼핑몰 할인쿠폰이 곧 추가됩니다', 
    'SHOPPING', 
    '추가 예정', 
    '미정',
    99999, 
    0, 
    'https://images.unsplash.com/photo-1472851294608-062f824d29cc?w=400', 
    30, 
    true, 
    false, 
    false, 
    0, 
    0, 
    NOW(), 
    NOW()
),
(
    'reward_shopping_02', 
    '편의점 상품권 추가 예정', 
    '편의점 이용권이 곧 추가됩니다', 
    'SHOPPING', 
    '추가 예정', 
    '미정',
    99999, 
    0, 
    'https://images.unsplash.com/photo-1472851294608-062f824d29cc?w=400', 
    30, 
    true, 
    true, 
    false, 
    0, 
    0, 
    NOW(), 
    NOW()
)
ON CONFLICT (id) DO UPDATE SET
    title = EXCLUDED.title,
    description = EXCLUDED.description,
    category = EXCLUDED.category,
    brand = EXCLUDED.brand,
    original_price = EXCLUDED.original_price,
    required_points = EXCLUDED.required_points,
    discount_percentage = EXCLUDED.discount_percentage,
    image_url = EXCLUDED.image_url,
    expiration_days = EXCLUDED.expiration_days,
    is_active = EXCLUDED.is_active,
    is_popular = EXCLUDED.is_popular,
    is_premium = EXCLUDED.is_premium,
    updated_at = NOW();


-- ===============================================
-- 데이터 확인 쿼리 (참고용)
-- ===============================================

-- SELECT '=== 템플릿 미션 수 ===' as info, COUNT(*) as count FROM missions WHERE is_template = true
-- UNION ALL
-- SELECT '=== 리워드 수 ===' as info, COUNT(*) as count FROM rewards;

-- ===============================================
-- 초기 데이터 설정 완료!
-- ===============================================
-- 이 스크립트로 다음이 생성됩니다:
-- - 10개의 템플릿 미션 (각 카테고리별)
-- - 6개의 플레이스홀더 리워드
-- 
-- 사용자는 회원가입을 통해 생성하세요!
-- 실행 후 애플리케이션에서 바로 테스트 가능합니다!