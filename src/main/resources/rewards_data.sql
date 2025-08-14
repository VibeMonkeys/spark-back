-- 리워드 샘플 데이터 추가
INSERT INTO rewards (
    id, title, description, category, brand, original_price, required_points, 
    discount_percentage, image_url, expiration_days, is_popular, is_premium, 
    is_active, total_exchanged, created_at, updated_at
) VALUES 
-- 기존 데이터 업데이트
('reward_01', '스타벅스 아메리카노', '전국 스타벅스에서 사용 가능한 아메리카노 쿠폰', 'COFFEE', '스타벅스', '4,500원', 350, 22, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a?w=400', 30, true, false, true, 142, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('reward_02', 'CGV 영화 관람권', '평일 2D 영화 관람 (팝콘 세트 포함)', 'ENTERTAINMENT', 'CGV', '12,000원', 900, 25, 'https://images.unsplash.com/photo-1489599743715-0a6c9f46b9e0?w=400', 60, false, false, true, 87, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('reward_03', 'GS25 편의점 상품권', '전국 GS25에서 사용 가능한 5,000원 상품권', 'SHOPPING', 'GS25', '5,000원', 400, 20, 'https://images.unsplash.com/photo-1570197506759-8b9de9b7a1b3?w=400', 90, true, false, true, 238, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- 새로운 리워드 추가
('reward_04', '투썸플레이스 케이크 세트', '아메리카노 + 시그니처 케이크', 'COFFEE', '투썸플레이스', '8,500원', 650, 23, 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400', 30, false, false, true, 62, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('reward_05', 'SPARK Premium 1개월', '무제한 리롤, 프리미엄 미션, 광고 제거', 'PREMIUM', 'SPARK', '4,900원', 1200, 0, 'https://images.unsplash.com/photo-1584515501397-335d595b2a17?w=400', 1, false, true, true, 34, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('reward_06', '교보문고 도서상품권', '전국 교보문고에서 사용 가능', 'SHOPPING', '교보문고', '10,000원', 750, 25, 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400', 180, false, false, true, 91, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('reward_07', '롯데리아 빅세트', '새우버거 + 감자튀김 + 콜라', 'FOOD', '롯데리아', '7,800원', 580, 26, 'https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400', 14, false, false, true, 156, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('reward_08', '배스킨라빈스 파인트', '좋아하는 맛 선택 가능', 'FOOD', '배스킨라빈스', '12,900원', 900, 30, 'https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=400', 30, true, false, true, 203, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('reward_09', '메가박스 영화관람권', '평일/주말 2D 영화 관람권', 'ENTERTAINMENT', '메가박스', '14,000원', 1100, 21, 'https://images.unsplash.com/photo-1489599743715-0a6c9f46b9e0?w=400', 90, false, false, true, 45, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('reward_10', '올리브영 뷰티 포인트', '온라인/오프라인 사용 가능', 'BEAUTY', '올리브영', '15,000원', 1200, 20, 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=400', 365, true, false, true, 178, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)

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
    is_popular = EXCLUDED.is_popular,
    is_premium = EXCLUDED.is_premium,
    is_active = EXCLUDED.is_active,
    total_exchanged = EXCLUDED.total_exchanged,
    updated_at = CURRENT_TIMESTAMP;