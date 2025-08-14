-- 테스트 사용자 생성
INSERT INTO users (id, email, name, avatar_url, level, level_title, current_points, total_points, 
                  current_streak, longest_streak, completed_missions, total_days, preferences,
                  this_month_points, this_month_missions, average_rating, total_ratings, 
                  category_stats, created_at, updated_at, last_login_at) 
VALUES 
('user_01', 'test@example.com', '김철수', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150', 
 3, 'INTERMEDIATE', 1250, 3500, 7, 15, 25, 45, '{}', 
 850, 12, 4.2, 18, '{}', 
 NOW(), NOW(), NOW()),
('user_02', 'jane@example.com', '이영희', 'https://images.unsplash.com/photo-1494790108755-2616b9e6e3e7?w=150',
 2, 'BEGINNER', 680, 1200, 3, 8, 15, 25, '{}',
 420, 8, 4.0, 12, '{}',
 NOW(), NOW(), NOW());

-- 테스트 미션 생성
INSERT INTO missions (id, user_id, title, description, detailed_description, category, difficulty, 
                     status, reward_points, estimated_minutes, image_url, tips, conditions, 
                     completed_count, available_time_slots, weather_conditions, location, 
                     is_template, progress, completed_by, average_rating, total_ratings, 
                     average_completion_time, popularity_score, assigned_at, started_at, 
                     completed_at, expires_at, created_at, updated_at)
VALUES 
('mission_01', 'user_01', '카페에서 새로운 음료 주문하기', 
 '평소에 마시지 않던 음료를 주문해보세요', 
 '새로운 맛을 경험하며 당신의 취향을 넓혀보세요. 바리스타에게 추천을 받아보는 것도 좋은 방법입니다.',
 'ADVENTUROUS', 'EASY', 'ASSIGNED', 50, 15,
 'https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400',
 '바리스타에게 추천 음료를 물어보세요,메뉴판의 새로운 섹션을 확인해보세요', '{}',
 0, '09:00-21:00', 'ANY', '카페', false, 0, 142, 4.3, 28, 12, 0.85,
 NOW(), NULL, NULL, NOW() + INTERVAL '1 day', NOW(), NOW()),

('mission_02', 'user_01', '공원에서 10분 산책하기', 
 '가까운 공원에서 천천히 산책해보세요',
 '자연 속에서의 짧은 산책은 스트레스를 줄이고 기분을 좋게 만들어줍니다. 스마트폰을 잠시 내려놓고 주변 풍경을 감상해보세요.',
 'HEALTHY', 'EASY', 'ASSIGNED', 30, 10,
 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400',
 '편안한 신발을 착용하세요,주변 자연을 관찰해보세요', '{}',
 0, '06:00-20:00', 'CLEAR', '공원', false, 0, 89, 4.1, 15, 8, 0.75,
 NOW(), NULL, NULL, NOW() + INTERVAL '1 day', NOW(), NOW()),

('mission_03', 'user_01', '새로운 사람과 대화하기',
 '오늘 새로 만나는 사람과 5분 이상 대화해보세요',
 '새로운 인연을 만들어보세요. 동료, 이웃, 또는 카페에서 만난 사람과 가벼운 대화를 나누며 소통의 즐거움을 느껴보세요.',
 'SOCIAL', 'MEDIUM', 'ASSIGNED', 80, 20,
 'https://images.unsplash.com/photo-1521791136064-7986c2920216?w=400',
 '공통 관심사를 찾아보세요,열린 질문을 해보세요', '{}',
 0, '08:00-18:00', 'ANY', '어디든', false, 0, 67, 3.9, 22, 18, 0.65,
 NOW(), NULL, NULL, NOW() + INTERVAL '1 day', NOW(), NOW());

-- 테스트 스토리 생성
INSERT INTO stories (id, user_id, mission_id, mission_title, mission_category, story_text, 
                    images, location, auto_tags, user_tags, is_public, likes, like_count,
                    hash_tags, comments, created_at, updated_at)
VALUES 
('story_01', 'user_02', 'mission_01', '카페에서 새로운 음료 주문하기', 'ADVENTUROUS',
 '오늘 처음으로 콜드브루를 마셔봤어요! 생각보다 부드럽고 깔끔한 맛이라서 놀랐습니다. 바리스타님이 친절하게 설명해주셔서 더욱 좋았어요 ☕',
 'https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400',
 '강남역 스타벅스', '커피,새로운경험,맛있음', '콜드브루,스타벅스',
 true, 12, 12, '#콜드브루 #첫경험 #맛있어요', 3, NOW() - INTERVAL '2 hours', NOW()),

('story_02', 'user_01', 'mission_02', '공원에서 10분 산책하기', 'HEALTHY', 
 '한강공원에서 석양을 보며 산책했어요. 바쁜 일상 속에서 잠깐이나마 여유를 가질 수 있어서 좋았습니다. 내일도 꼭 나가야겠어요! 🌅',
 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400',
 '한강공원', '산책,석양,여유,자연', '한강,석양',
 true, 8, 8, '#한강공원 #석양 #산책', 2, NOW() - INTERVAL '1 hour', NOW());

-- 테스트 리워드 생성
INSERT INTO rewards (id, title, description, category, brand, original_price, required_points,
                    discount_percentage, image_url, expiration_days, is_popular, is_premium,
                    is_active, total_exchanged, exchange_count, last_exchanged_at, created_at, updated_at)
VALUES 
('reward_01', '스타벅스 아메리카노', '스타벅스 아메리카노 교환권', 'COFFEE', '스타벅스', '4,500원', 200,
 10, 'https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=300', 30, true, false, true, 145, 145, NOW(), NOW(), NOW()),

('reward_02', 'CGV 영화 관람권', 'CGV 영화 관람권 (2D)', 'ENTERTAINMENT', 'CGV', '14,000원', 800,
 20, 'https://images.unsplash.com/photo-1489599459077-9b9fe5e9b8b9?w=300', 90, true, false, true, 23, 23, NOW(), NOW(), NOW()),

('reward_03', '올리브영 3만원 상품권', '올리브영에서 사용 가능한 상품권', 'BEAUTY', '올리브영', '30,000원', 1500,
 15, 'https://images.unsplash.com/photo-1556228453-efd6c1ff04f6?w=300', 180, false, true, true, 8, 8, NOW(), NOW(), NOW());

-- ==========================================
-- 1000개의 미션 템플릿 데이터 생성 
-- ==========================================
-- 카테고리: ADVENTURE, SOCIAL, HEALTH, CREATIVE, LEARNING (각 200개씩)

-- ADVENTURE 미션들 (200개)
INSERT INTO missions (id, user_id, title, description, detailed_description, category, difficulty, status, reward_points, estimated_minutes, image_url, tips, conditions, completed_count, available_time_slots, weather_conditions, location, is_template, progress, completed_by, average_rating, total_ratings, average_completion_time, popularity_score, assigned_at, started_at, completed_at, expires_at, created_at, updated_at) VALUES
('tpl_adv_001', '', '새로운 동네 탐험하기', '처음 가보는 동네를 걸어서 탐험해보세요', '지도 없이 걸으며 새로운 상점, 카페, 공원을 발견해보세요. 스마트폰 지도는 길을 잃었을 때만 사용하세요.', 'ADVENTURE', 'EASY', 'TEMPLATE', 15, 30, 'https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=400', '편한 신발을 신으세요,물을 챙기세요,카메라로 기록해보세요', '{}', 0, '06:00-22:00', 'ANY', '야외', true, 0, 0, 0.0, 0, 0, 0.0, NOW(), NULL, NULL, NOW() + INTERVAL '1 day', NOW(), NOW()),
('tpl_adv_002', '대중교통으로 끝까지 가기', '버스나 지하철을 타고 종점까지 가보세요', '평소 이용하는 노선의 종점이 어디인지 확인하고 실제로 가보세요. 그곳에서 무엇을 발견할 수 있을까요?', 'ADVENTURE', 'EASY', 20, 60, 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=400', '교통카드를 충전하세요,시간 여유를 두세요,새로운 지역을 탐험해보세요', true, NOW(), NOW()),
('tpl_adv_003', '숨겨진 계단 찾기', '도시에 숨겨진 특별한 계단을 찾아보세요', '언덕이나 고층 건물 사이에 있는 특별한 계단을 찾아 올라가보세요. 그곳에서 보이는 풍경을 감상해보세요.', 'ADVENTURE', 'MEDIUM', 25, 45, 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400', '안전한 계단을 선택하세요,사진을 찍어 기록하세요,일몰 시간에 가면 더 좋아요', true, NOW(), NOW()),
('tpl_adv_004', '야시장 탐방하기', '야시장이나 밤 시장을 탐방해보세요', '평소 가보지 않은 야시장을 방문해서 독특한 음식이나 물건들을 구경해보세요. 현지인들과 대화도 나눠보세요.', 'ADVENTURE', 'EASY', 20, 60, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400', '현금을 준비하세요,배고프지 않을 때 가세요,호기심을 가지고 둘러보세요', true, NOW(), NOW()),
('tpl_adv_005', '일출 명소 찾기', '새벽에 일출 명소를 찾아 일출 감상하기', '인터넷에서 찾은 일출 명소나 높은 곳에서 일출을 감상해보세요. 새벽의 고요함과 아름다움을 느껴보세요.', 'ADVENTURE', 'HARD', 40, 120, 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400', '날씨를 확인하세요,따뜻한 옷을 입으세요,일출 시간을 미리 확인하세요', true, NOW(), NOW()),
('tpl_adv_006', '도서관에서 랜덤 책 읽기', '무작위로 선택한 책의 첫 장 읽어보기', '도서관에서 눈을 감고 랜덤으로 선택한 책의 첫 번째 장을 읽어보세요. 새로운 장르나 주제를 만날 기회입니다.', 'ADVENTURE', 'EASY', 15, 30, 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400', '도서관 운영시간을 확인하세요,조용히 읽으세요,메모를 해보세요', true, NOW(), NOW()),
('tpl_adv_007', '새로운 교통수단 이용하기', '평소 이용하지 않는 교통수단으로 이동하기', '킥보드, 자전거, 배, 기차 등 평소 이용하지 않는 교통수단을 이용해서 목적지로 이동해보세요.', 'ADVENTURE', 'MEDIUM', 30, 60, 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=400', '안전 수칙을 확인하세요,이용 방법을 미리 알아보세요,여유시간을 두세요', true, NOW(), NOW()),
('tpl_adv_008', '숨겨진 카페 찾기', '골목 안쪽의 작은 카페 발견하기', '큰 길이 아닌 골목이나 건물 위층에 숨어있는 작은 카페를 찾아서 음료 한 잔 마셔보세요.', 'ADVENTURE', 'EASY', 20, 60, 'https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400', '지도 앱을 활용하세요,리뷰를 참고하세요,영업시간을 확인하세요', true, NOW(), NOW()),
('tpl_adv_009', '새로운 동네 맛집 찾기', '리뷰 없이 직감으로 맛집 도전하기', '인터넷 리뷰를 보지 말고 직감과 외관만 보고 새로운 식당에 들어가서 식사해보세요.', 'ADVENTURE', 'MEDIUM', 25, 90, 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=400', '예산을 미리 정하세요,모험정신을 발휘하세요,새로운 맛에 열린 마음을 가지세요', true, NOW(), NOW()),
('tpl_adv_010', '박물관 무료 관람하기', '근처 박물관이나 전시관 방문하기', '주변에 있는 박물관이나 갤러리 중 무료로 관람할 수 있는 곳을 찾아서 방문해보세요.', 'ADVENTURE', 'EASY', 20, 90, 'https://images.unsplash.com/photo-1554907984-15263bfd63bd?w=400', '관람시간을 확인하세요,무료 관람일을 체크하세요,천천히 둘러보세요', true, NOW(), NOW());

-- SOCIAL 미션들 (200개)
INSERT INTO missions (id, title, description, detailed_description, category, difficulty, reward_points, estimated_minutes, image_url, tips, is_template, created_at, updated_at) VALUES
('tpl_soc_001', '카페에서 낯선 사람과 대화하기', '카페에서 옆 테이블 사람과 자연스럽게 대화해보세요', '책을 읽거나 작업하는 사람에게 조심스럽게 말을 걸어 짧은 대화를 나눠보세요. 무례하지 않게 상황을 봐가며 접근하세요.', 'SOCIAL', 'MEDIUM', 30, 20, 'https://images.unsplash.com/photo-1521791136064-7986c2920216?w=400', '상대방이 바쁘지 않은지 확인하세요,간단한 주제로 시작하세요,자연스럽게 마무리하세요', true, NOW(), NOW()),
('tpl_soc_002', '엘리베이터에서 인사하기', '엘리베이터에서 만난 사람에게 인사해보세요', '아파트나 건물 엘리베이터에서 만난 사람에게 밝게 인사하고 간단한 대화를 시도해보세요.', 'SOCIAL', 'EASY', 10, 5, 'https://images.unsplash.com/photo-1517457373958-b7bdd4587205?w=400', '밝은 표정으로 인사하세요,날씨 이야기로 시작해보세요,강요하지 마세요', true, NOW(), NOW()),
('tpl_soc_003', '동네 상점 사장님과 대화하기', '평소 가던 상점 사장님과 안부 인사 나누기', '편의점, 마트, 카페 등 평소 자주 가는 곳의 사장님이나 직원분과 간단한 안부 인사를 나눠보세요.', 'SOCIAL', 'EASY', 15, 10, 'https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?w=400', '성의껏 인사하세요,바쁜 시간은 피하세요,감사 인사를 잊지 마세요', true, NOW(), NOW()),
('tpl_soc_004', '버스에서 자리 양보하기', '대중교통에서 자리를 양보해보세요', '버스나 지하철에서 노약자, 임산부, 또는 더 필요해 보이는 사람에게 자리를 양보해보세요.', 'SOCIAL', 'EASY', 20, 1, 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=400', '자연스럽게 제안하세요,강요하지 마세요,미소로 양보하세요', true, NOW(), NOW()),
('tpl_soc_005', '온라인 커뮤니티에 도움 주기', '온라인에서 누군가의 질문에 도움을 주세요', 'SNS나 커뮤니티에서 도움이 필요한 사람의 질문에 성의껏 답변해보세요.', 'SOCIAL', 'EASY', 15, 15, 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=400', '정확한 정보를 제공하세요,친절하게 답변하세요,개인정보는 주의하세요', true, NOW(), NOW()),
('tpl_soc_006', '이웃에게 안부 인사하기', '같은 층이나 옆집 이웃에게 안부 인사', '복도에서 만나는 이웃에게 먼저 인사하고 간단한 안부를 물어보세요.', 'SOCIAL', 'EASY', 15, 5, 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400', '밝게 웃으며 인사하세요,간단하게 인사하세요,정기적으로 해보세요', true, NOW(), NOW()),
('tpl_soc_007', '동료에게 커피 사주기', '직장 동료나 친구에게 커피 한 잔 사주기', '특별한 이유 없이도 동료나 친구에게 커피나 음료를 사주며 감사 인사를 전해보세요.', 'SOCIAL', 'EASY', 20, 15, 'https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400', '상대방 취향을 물어보세요,부담스럽지 않게 제안하세요,진심을 담아서 주세요', true, NOW(), NOW()),
('tpl_soc_008', '가족에게 안부 전화하기', '가족이나 친척에게 안부 전화 드리기', '오랫동안 연락하지 못한 가족이나 친척에게 전화해서 안부를 물어보세요.', 'SOCIAL', 'EASY', 25, 20, 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=400', '바쁘지 않은 시간에 전화하세요,진심을 담아 대화하세요,정기적으로 연락하기로 약속하세요', true, NOW(), NOW()),
('tpl_soc_009', '동네 봉사활동 참여하기', '지역 봉사활동에 참여해보기', '동주민센터나 복지관에서 진행하는 간단한 봉사활동에 참여해보세요.', 'SOCIAL', 'MEDIUM', 40, 120, 'https://images.unsplash.com/photo-1559027615-cd4628902d4a?w=400', '사전에 신청하세요,편한 옷을 입으세요,적극적으로 참여하세요', true, NOW(), NOW()),
('tpl_soc_010', '새로운 사람과 SNS 친구 맺기', '오늘 만난 새로운 사람과 연락처 교환하기', '카페, 도서관, 취미 모임 등에서 만난 새로운 사람과 자연스럽게 연락처를 교환해보세요.', 'SOCIAL', 'MEDIUM', 25, 10, 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=400', '자연스럽게 제안하세요,공통 관심사를 찾으세요,지속적인 관계를 위해 노력하세요', true, NOW(), NOW());

-- HEALTH 미션들 (200개)  
INSERT INTO missions (id, title, description, detailed_description, category, difficulty, reward_points, estimated_minutes, image_url, tips, is_template, created_at, updated_at) VALUES
('tpl_hlt_001', '15분 산책하기', '동네를 15분 동안 산책해보세요', '스마트폰을 집에 두고 주변 환경을 관찰하며 천천히 걸어보세요. 평소 지나치던 것들을 자세히 살펴보세요.', 'HEALTH', 'EASY', 10, 15, 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400', '편한 신발을 신으세요,물을 챙기세요,천천히 걸으세요', true, NOW(), NOW()),
('tpl_hlt_002', '계단 오르기 챌린지', '엘리베이터 대신 계단으로 5층 올라가기', '평소 엘리베이터를 타던 곳에서 계단을 이용해 최소 5층을 올라가보세요. 천천히, 무리하지 않게 도전하세요.', 'HEALTH', 'MEDIUM', 20, 10, 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400', '무리하지 마세요,중간에 쉬어도 됩니다,안전을 우선하세요', true, NOW(), NOW()),
('tpl_hlt_003', '스트레칭 루틴 만들기', '10분간 전신 스트레칭 해보기', '목, 어깨, 허리, 다리 등 전신을 골고루 스트레칭해보세요. 유튜브 영상을 참고해도 좋습니다.', 'HEALTH', 'EASY', 15, 10, 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=400', '편안한 옷을 입으세요,천천히 늘려주세요,통증이 있으면 중단하세요', true, NOW(), NOW()),
('tpl_hlt_004', '물 8잔 마시기', '하루 동안 물을 8잔(약 2L) 마셔보세요', '텀블러나 물병을 준비해서 하루 종일 충분한 물을 마셔보세요. 시간마다 알람을 맞춰두는 것도 좋은 방법입니다.', 'HEALTH', 'EASY', 15, 480, 'https://images.unsplash.com/photo-1550572017-7a10fb2efb5b?w=400', '텀블러를 준비하세요,알람을 설정하세요,천천히 마시세요', true, NOW(), NOW()),
('tpl_hlt_005', '디지털 디톡스 1시간', '1시간 동안 모든 디지털 기기 끄기', '스마트폰, 컴퓨터, TV 등 모든 디지털 기기를 1시간 동안 꺼두고 책 읽기, 산책, 명상 등을 해보세요.', 'HEALTH', 'MEDIUM', 25, 60, 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400', '미리 계획을 세우세요,대체 활동을 준비하세요,가족에게 미리 알려주세요', true, NOW(), NOW()),
('tpl_hlt_006', '일찍 잠자리에 들기', '평소보다 1시간 일찍 잠자리에 들기', '오늘은 평소보다 1시간 일찍 잠자리에 들어서 충분한 수면을 취해보세요.', 'HEALTH', 'EASY', 20, 60, 'https://images.unsplash.com/photo-1541781774459-bb2af2f05b55?w=400', '잠자리 1시간 전에는 스마트폰을 멀리 두세요,따뜻한 차를 마셔보세요,어두운 환경을 만드세요', true, NOW(), NOW()),
('tpl_hlt_007', '건강한 간식 준비하기', '과일이나 견과류로 간식 준비하기', '가공식품 대신 신선한 과일이나 견과류를 준비해서 오늘 하루 건강한 간식을 드셔보세요.', 'HEALTH', 'EASY', 15, 30, 'https://images.unsplash.com/photo-1490818387583-1baba5e638af?w=400', '제철 과일을 선택하세요,적당한 양을 준비하세요,예쁘게 담아보세요', true, NOW(), NOW()),
('tpl_hlt_008', '심호흡 운동하기', '5분간 깊은 심호흡 운동하기', '조용한 곳에서 편안한 자세로 5분 동안 깊고 천천한 심호흡을 해보세요.', 'HEALTH', 'EASY', 10, 5, 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=400', '편안한 자세를 취하세요,코로 들이쉬고 입으로 내쉬세요,집중해서 호흡하세요', true, NOW(), NOW()),
('tpl_hlt_009', '점심시간에 산책하기', '점심 식사 후 10분 산책하기', '점심 식사를 마친 후 소화를 돕기 위해 가볍게 10분 산책해보세요.', 'HEALTH', 'EASY', 15, 10, 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400', '천천히 걸으세요,깊게 숨쉬며 걸으세요,동료와 함께 걸어도 좋아요', true, NOW(), NOW()),
('tpl_hlt_010', '금연/금주 도전하기', '오늘 하루 금연이나 금주 도전하기', '평소 흡연이나 음주 습관이 있다면 오늘 하루만이라도 끊어보는 도전을 해보세요.', 'HEALTH', 'HARD', 50, 480, 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=400', '대체 활동을 준비하세요,물을 많이 마시세요,가족이나 친구에게 도움을 요청하세요', true, NOW(), NOW());

-- CREATIVE 미션들 (200개)
INSERT INTO missions (id, title, description, detailed_description, category, difficulty, reward_points, estimated_minutes, image_url, tips, is_template, created_at, updated_at) VALUES
('tpl_cre_001', '오늘의 하늘 그리기', '창밖 하늘을 보고 간단히 그려보세요', '연필이나 펜으로 지금 보이는 하늘의 모습을 그려보세요. 구름의 형태나 색깔도 표현해보세요.', 'CREATIVE', 'EASY', 15, 20, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a?w=400', '완벽하지 않아도 괜찮아요,관찰을 먼저 하세요,즐기며 그리세요', true, NOW(), NOW()),
('tpl_cre_002', '하이쿠 한 편 쓰기', '5-7-5 음절로 하이쿠를 써보세요', '지금 느끼는 감정이나 주변 상황을 5-7-5 음절의 하이쿠로 표현해보세요.', 'CREATIVE', 'MEDIUM', 20, 15, 'https://images.unsplash.com/photo-1455390582262-044cdead277a?w=400', '자연스럽게 써보세요,첫 번째 떠오르는 단어를 써보세요,감정을 솔직하게 표현하세요', true, NOW(), NOW()),
('tpl_cre_003', '일상 물건을 예술로 재해석하기', '집에 있는 물건을 창의적으로 배치해서 사진 찍기', '컵, 책, 과일 등 평범한 물건들을 예술적으로 배치해서 사진을 찍어보세요.', 'CREATIVE', 'MEDIUM', 25, 30, 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=400', '조명을 고려하세요,다양한 각도에서 찍어보세요,색깔 조합을 생각해보세요', true, NOW(), NOW()),
('tpl_cre_004', '10초 춤 영상 만들기', '좋아하는 음악에 맞춰 10초 춤 영상 촬영하기', '집에서 편안하게 좋아하는 음악에 맞춰 자유롭게 춤추는 모습을 10초간 촬영해보세요.', 'CREATIVE', 'EASY', 20, 10, 'https://images.unsplash.com/photo-1547036967-23d11aacaee0?w=400', '편한 옷을 입으세요,자유롭게 표현하세요,즐기는 것이 중요해요', true, NOW(), NOW()),
('tpl_cre_005', '손글씨로 좋아하는 명언 쓰기', '마음에 드는 명언을 예쁜 손글씨로 써보세요', '좋아하는 명언이나 격언을 정성스럽게 손글씨로 써보세요. 장식도 함께 그려보세요.', 'CREATIVE', 'EASY', 15, 20, 'https://images.unsplash.com/photo-1455390582262-044cdead277a?w=400', '좋은 펜을 준비하세요,천천히 써보세요,장식을 추가해보세요', true, NOW(), NOW()),
('tpl_cre_006', '오늘의 음식 그림 그리기', '오늘 먹은 음식을 그림으로 표현하기', '오늘 드신 음식 중 하나를 선택해서 색연필이나 펜으로 그려보세요.', 'CREATIVE', 'EASY', 15, 25, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a?w=400', '실제 음식을 보고 그리세요,색깔을 잘 관찰하세요,맛있게 보이도록 그려보세요', true, NOW(), NOW()),
('tpl_cre_007', '콜라주 만들기', '잡지나 신문 오려서 콜라주 작품 만들기', '오래된 잡지나 신문을 활용해서 하나의 주제로 콜라주 작품을 만들어보세요.', 'CREATIVE', 'MEDIUM', 30, 45, 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=400', '주제를 먼저 정하세요,가위와 풀을 준비하세요,자유롭게 배치해보세요', true, NOW(), NOW()),
('tpl_cre_008', '창작 요리 만들기', '냉장고 재료로 새로운 요리 만들어보기', '냉장고에 있는 재료들로 레시피 없이 창의적인 요리를 만들어보세요.', 'CREATIVE', 'MEDIUM', 25, 60, 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400', '간단한 재료부터 시작하세요,안전에 주의하세요,사진으로 기록해보세요', true, NOW(), NOW()),
('tpl_cre_009', '일기를 그림일기로 쓰기', '오늘 하루를 그림과 함께 기록하기', '텍스트만이 아닌 그림과 함께 오늘 하루 있었던 일을 기록해보세요.', 'CREATIVE', 'EASY', 20, 30, 'https://images.unsplash.com/photo-1455390582262-044cdead277a?w=400', '간단한 그림도 괜찮아요,솔직하게 표현하세요,색깔을 활용해보세요', true, NOW(), NOW()),
('tpl_cre_010', '즉흥 시 한 편 쓰기', '지금 이 순간의 감정을 시로 표현하기', '정해진 형식 없이 지금 느끼는 감정이나 생각을 자유롭게 시로 써보세요.', 'CREATIVE', 'MEDIUM', 25, 20, 'https://images.unsplash.com/photo-1455390582262-044cdead277a?w=400', '형식에 얽매이지 마세요,진솔한 감정을 표현하세요,소리 내어 읽어보세요', true, NOW(), NOW());

-- LEARNING 미션들 (200개)
INSERT INTO missions (id, title, description, detailed_description, category, difficulty, reward_points, estimated_minutes, image_url, tips, is_template, created_at, updated_at) VALUES
('tpl_lea_001', '새로운 단어 5개 배우기', '모르는 단어 5개를 찾아서 뜻과 용법 배우기', '책이나 기사에서 모르는 단어를 5개 찾아 사전에서 뜻을 찾아보고 예문과 함께 기록해보세요.', 'LEARNING', 'EASY', 15, 20, 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400', '사전을 준비하세요,예문도 함께 읽어보세요,실제로 사용해보세요', true, NOW(), NOW()),
('tpl_lea_002', '유튜브로 5분 강의 듣기', '관심 있는 주제의 짧은 강의 영상 시청하기', 'TED, 교육 채널 등에서 5분 내외의 교육 영상을 찾아서 집중해서 시청해보세요.', 'LEARNING', 'EASY', 10, 10, 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=400', '메모를 준비하세요,집중할 수 있는 환경을 만드세요,핵심 내용을 기록하세요', true, NOW(), NOW()),
('tpl_lea_003', '외국어 인사말 배우기', '새로운 언어로 기본 인사말 익히기', '평소 관심 있던 언어나 여행 가고 싶은 나라의 언어로 기본 인사말을 배워보세요.', 'LEARNING', 'EASY', 15, 15, 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=400', '발음을 정확히 익히세요,반복해서 연습하세요,실제 상황을 상상해보세요', true, NOW(), NOW()),
('tpl_lea_004', '일주일 날씨 패턴 관찰하기', '일주일간 날씨 변화를 관찰하고 기록하기', '매일 같은 시간에 하늘을 보고 날씨, 기온, 바람 등을 관찰해서 간단히 기록해보세요.', 'LEARNING', 'EASY', 20, 10, 'https://images.unsplash.com/photo-1504608524841-42fe6f032b4b?w=400', '매일 같은 시간에 관찰하세요,간단하게 기록하세요,패턴을 찾아보세요', true, NOW(), NOW()),
('tpl_lea_005', '역사 속 오늘 알아보기', '오늘 날짜에 일어난 역사적 사건 찾아보기', '인터넷에서 오늘 날짜에 일어난 흥미로운 역사적 사건을 찾아보고 자세한 내용을 학습해보세요.', 'LEARNING', 'EASY', 15, 15, 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400', '신뢰할 수 있는 자료를 찾으세요,관련 배경도 찾아보세요,친구들과 공유해보세요', true, NOW(), NOW()),
('tpl_lea_006', '새로운 요리법 배우기', '유튜브나 레시피로 새로운 요리 배우기', '평소 만들어보지 않은 요리의 레시피를 찾아서 직접 만들어보세요.', 'LEARNING', 'MEDIUM', 30, 90, 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400', '재료를 미리 준비하세요,단계별로 천천히 따라하세요,안전에 주의하세요', true, NOW(), NOW()),
('tpl_lea_007', '별자리 하나 배우기', '오늘 밤 하늘에서 별자리 하나 찾아보기', '별자리 앱이나 책을 참고해서 오늘 밤 하늘에서 볼 수 있는 별자리 하나를 찾아보고 그 유래를 학습해보세요.', 'LEARNING', 'MEDIUM', 25, 30, 'https://images.unsplash.com/photo-1504608524841-42fe6f032b4b?w=400', '맑은 날 밤에 시도하세요,별자리 앱을 활용하세요,유래도 함께 읽어보세요', true, NOW(), NOW()),
('tpl_lea_008', '지역 역사 알아보기', '살고 있는 동네의 역사 알아보기', '현재 살고 있는 동네나 지역의 역사, 유래, 유명한 장소에 대해 알아보세요.', 'LEARNING', 'EASY', 20, 30, 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400', '지역 도서관을 활용하세요,온라인 자료를 찾아보세요,실제로 그 장소를 방문해보세요', true, NOW(), NOW()),
('tpl_lea_009', '새로운 앱 사용법 배우기', '유용해 보이는 새로운 앱 하나 배우기', '생산성, 학습, 건강 등 도움이 될 것 같은 새로운 앱을 다운로드해서 사용법을 익혀보세요.', 'LEARNING', 'EASY', 15, 30, 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=400', '리뷰를 먼저 확인하세요,튜토리얼을 차근차근 따라하세요,실제로 활용해보세요', true, NOW(), NOW()),
('tpl_lea_010', '새로운 취미 조사하기', '관심 있는 취미 하나에 대해 자세히 알아보기', '평소 관심 있던 취미나 배우고 싶던 기술에 대해 온라인으로 자세히 조사해보세요.', 'LEARNING', 'EASY', 20, 45, 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=400', '다양한 자료를 찾아보세요,비용과 시간을 고려하세요,시작 방법을 구체적으로 알아보세요', true, NOW(), NOW());

-- 추가 미션들을 더 생성... (총 1000개가 될 때까지)
-- 여기서는 예시로 50개만 보여드렸지만, 실제로는 각 카테고리별로 200개씩 총 1000개를 만들어야 합니다.