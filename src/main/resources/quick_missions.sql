-- 기존 템플릿 미션 삭제
DELETE FROM missions WHERE is_template = true;

-- 50개의 다양한 템플릿 미션 추가 (모든 NOT NULL 필드 포함)
-- ID는 PostgreSQL 시퀀스에 의해 자동 생성되므로 제외
INSERT INTO missions (
    user_id, title, description, detailed_description, category, difficulty, 
    reward_points, estimated_minutes, image_url, tips, is_template, status, 
    progress, completed_by, average_rating, total_ratings, average_completion_time, 
    popularity_score, assigned_at, expires_at, created_at, updated_at
) VALUES

-- SOCIAL 미션들 (10개)
(NULL, '카페에서 직원과 인사하기', '카페에서 주문할 때 직원분께 밝게 인사해보세요', '평소보다 조금 더 밝은 목소리로 안녕하세요라고 인사하고, 주문 후 감사합니다를 꼭 말해보세요.', 'SOCIAL', 'EASY', 10, 10, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '자연스럽고 진심 어린 인사를 해보세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '엘리베이터에서 인사하기', '엘리베이터를 타면서 다른 사람과 자연스럽게 인사해보세요', '엘리베이터에 타면서 이미 타고 있는 사람이나 함께 타는 사람에게 가볍게 인사해보세요.', 'SOCIAL', 'EASY', 10, 10, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '자연스럽게 눈 맞춤하며 인사하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '대중교통에서 자리 양보하기', '도움이 필요한 분께 자리를 양보해보세요', '임산부, 어르신, 아이를 안고 계신 분 등 도움이 필요해 보이는 분께 자연스럽게 자리를 양보해보세요.', 'SOCIAL', 'EASY', 15, 15, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '자연스럽게 일어나서 양보하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '카페에서 옆 테이블과 대화하기', '카페에서 혼자 앉아있는 분과 자연스럽게 대화해보세요', '카페에서 혼자 앉아있는 분께 자연스럽게 다가가 간단한 인사나 날씨 얘기부터 시작해보세요.', 'SOCIAL', 'MEDIUM', 20, 20, 'https://images.unsplash.com/photo-1655579932488-e05b9f649ede', '날씨나 카페 분위기 같은 자연스러운 주제로 시작하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '동네 상점 사장님과 안부 묻기', '자주 가는 가게 사장님께 안부를 물어보세요', '자주 이용하는 편의점, 카페, 식당 사장님께 오늘 하루 어떠셨어요? 같은 안부를 물어보고 짧은 대화를 나눠보세요.', 'SOCIAL', 'MEDIUM', 20, 20, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '진심 어린 관심을 보여주세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '새로운 취미 모임 찾아보기', '온라인으로 새로운 취미 모임을 찾아보고 가입 문의하기', '관심 있는 취미나 활동 관련 모임을 온라인에서 찾아보고, 실제로 연락해서 참여 방법을 문의해보세요.', 'SOCIAL', 'MEDIUM', 25, 25, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '관심사와 맞는 모임을 찾으세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '모르는 사람과 프리스비 하기', '공원에서 모르는 사람과 함께 프리스비를 해보세요', '한강공원이나 넓은 공원에서 프리스비나 공을 가져가 혼자 던지고 받기를 하다가, 지나가는 사람들에게 함께 할 것을 제안해보세요.', 'SOCIAL', 'HARD', 30, 30, 'https://images.unsplash.com/photo-1584515501397-335d595b2a17', '안전한 공원에서 진행하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '새로운 동네 친구 만들기', '동네에서 새로운 친구를 만들어보세요', '동네 모임, 카페, 공원 등에서 비슷한 관심사를 가진 사람과 친구가 되어 연락처를 교환해보세요.', 'SOCIAL', 'HARD', 35, 35, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '진심 어린 관심을 보이세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '영어로 외국인과 대화하기', '외국인 관광객과 영어로 5분 이상 대화해보세요', '관광지나 지하철에서 길을 찾는 외국인 관광객을 발견하면 영어로 도움을 주고 한국에 대한 대화를 나누어보세요.', 'SOCIAL', 'HARD', 35, 35, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '완벽한 영어가 아니어도 괜찮아요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '지역 봉사활동 참여하기', '지역 사회 봉사활동에 참여해보세요', '동네 복지관, 환경단체, 자원봉사센터 등에서 하는 봉사활동에 참여해 새로운 사람들과 의미 있는 시간을 보내세요.', 'SOCIAL', 'HARD', 40, 40, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '지속적으로 참여할 수 있는 활동을 선택하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

-- ADVENTURE 미션들 (10개)
(NULL, '버스 대신 한 정거장 걸어가기', '걸으면서 동네를 새롭게 관찰해보세요', '평소 버스나 지하철을 타고 가던 곳 중 한 정거장을 걸어서 가보세요. 천천히 걸으며 평소에 놓쳤던 가게들, 건물들, 사람들을 관찰해보세요.', 'ADVENTURE', 'EASY', 10, 10, 'https://images.unsplash.com/photo-1584515501397-335d595b2a17', '시간 여유가 있을 때 진행하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '새로운 지하철 역 탐방하기', '가본 적 없는 지하철 역에서 내려서 주변을 둘러보세요', '지하철 노선도에서 가본 적 없는 역을 선택해서 내려보고, 역 주변을 30분 정도 걸어보세요.', 'ADVENTURE', 'EASY', 15, 15, 'https://images.unsplash.com/photo-1584515501397-335d595b2a17', '대중교통으로 쉽게 돌아올 수 있는 곳을 선택하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '새로운 음식점 도전하기', '한 번도 안 가본 음식점에서 식사해보세요', '동네에 있지만 한 번도 가보지 않은 음식점이나 처음 보는 요리를 파는 곳에서 식사해보세요.', 'ADVENTURE', 'EASY', 15, 15, 'https://images.unsplash.com/photo-1584515501397-335d595b2a17', '리뷰를 미리 확인해보세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '평소와 다른 시간에 산책하기', '일찍 일어나서 새벽 산책을 해보세요', '평소보다 2시간 일찍 일어나서 새벽이나 이른 아침에 동네를 산책해보세요. 평소와 다른 동네의 모습을 발견해보세요.', 'ADVENTURE', 'EASY', 15, 15, 'https://images.unsplash.com/photo-1584515501397-335d595b2a17', '안전한 시간과 장소를 선택하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '가보지 않은 길로 퇴근하기', '평소와 다른 길을 선택해서 새로운 풍경을 만나보세요', '오늘은 평소 다니던 길 대신 새로운 경로를 선택해보세요. 지도를 보지 말고 직감을 따라 걸어보거나, 평소에 지나치기만 했던 골목길로 들어가 보세요.', 'ADVENTURE', 'MEDIUM', 20, 20, 'https://images.unsplash.com/photo-1584515501397-335d595b2a17', '안전한 시간대와 장소를 선택하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '새로운 동네 카페 5곳 방문하기', '하루에 다른 동네 카페 5곳을 방문해보세요', '평소에 가지 않던 동네로 가서 카페 5곳을 방문해 각각 다른 음료를 주문하고 분위기를 느껴보세요.', 'ADVENTURE', 'MEDIUM', 25, 25, 'https://images.unsplash.com/photo-1584515501397-335d595b2a17', '카페 사이 거리를 고려해서 경로를 짜보세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '대중교통으로 2시간 거리 여행', '대중교통만 이용해서 2시간 거리에 있는 곳을 다녀오세요', '지하철이나 버스만 이용해서 집에서 2시간 정도 걸리는 곳으로 당일치기 여행을 떠나보세요.', 'ADVENTURE', 'MEDIUM', 30, 30, 'https://images.unsplash.com/photo-1584515501397-335d595b2a17', '교통편과 시간표를 미리 확인하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '새로운 동네 탐험하기', '가본 적 없는 동네에서 2시간 보내기', '지하철이나 버스를 타고 가본 적 없는 동네로 가보세요. 최소 2시간 동안 그 동네를 걸어다니며 카페, 맛집, 상점들을 둘러보고 그 동네만의 특색을 찾아보세요.', 'ADVENTURE', 'HARD', 35, 35, 'https://images.unsplash.com/photo-1584515501397-335d595b2a17', '대중교통으로 접근 가능한 곳을 선택하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '랜덤 버스 타고 모험하기', '번호를 랜덤으로 선택해서 그 버스의 종점까지 가보세요', '1-999 사이의 번호를 랜덤으로 선택한 후, 그 번호의 버스가 있으면 타고 종점까지 가서 주변을 둘러보세요.', 'ADVENTURE', 'HARD', 35, 35, 'https://images.unsplash.com/photo-1584515501397-335d595b2a17', '돌아올 교통편을 미리 확인하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '서울 야경 명소 5곳 방문하기', '서울의 유명한 야경 명소 5곳을 하루에 방문해보세요', '남산타워, 반포 무지개다리, 여의도 한강공원, 청계천, 동대문 DDP 등 야경이 아름다운 곳 5곳을 선택해서 하루에 모두 방문해보세요.', 'ADVENTURE', 'HARD', 40, 40, 'https://images.unsplash.com/photo-1584515501397-335d595b2a17', '교통편과 이동 경로를 미리 계획하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

-- HEALTH 미션들 (10개)
(NULL, '계단으로 5층 올라가기', '엘리베이터 대신 계단을 이용해 건강한 하루를 시작하세요', '오늘 엘리베이터를 타지 말고 계단으로 5층까지 올라가보세요. 천천히, 자신의 페이스에 맞춰 올라가며 몸의 변화를 느껴보세요.', 'HEALTH', 'EASY', 10, 10, 'https://images.unsplash.com/photo-1597644568217-780bd0b0efb2', '무릎이 아프면 중간에 쉬어가세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '15분 가벼운 스트레칭', '15분 동안 전신 스트레칭을 해보세요', '목, 어깨, 허리, 다리 등 전신을 15분 동안 천천히 스트레칭해보세요. 유튜브 영상을 참고해도 좋습니다.', 'HEALTH', 'EASY', 10, 10, 'https://images.unsplash.com/photo-1597644568217-780bd0b0efb2', '무리하지 말고 천천히 하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '물 2리터 마시기', '하루 동안 물 2리터를 마셔보세요', '하루 동안 물을 2리터 마시는 것을 목표로 해보세요. 텀블러에 물을 담아서 조금씩 자주 마셔보세요.', 'HEALTH', 'EASY', 10, 10, 'https://images.unsplash.com/photo-1597644568217-780bd0b0efb2', '한 번에 많이 마시지 말고 조금씩 자주 마시세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '10분 명상하기', '조용한 곳에서 10분 동안 명상해보세요', '조용한 곳에 앉아서 눈을 감고 호흡에만 집중하며 10분 동안 명상해보세요.', 'HEALTH', 'EASY', 15, 15, 'https://images.unsplash.com/photo-1597644568217-780bd0b0efb2', '편안한 자세로 앉으세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '30분 동안 산책하기', '근처 공원이나 한적한 길을 걸으며 마음을 비워보세요', '30분 동안 천천히 걸으며 주변 풍경을 감상하고 깊게 숨을 쉬어보세요. 스마트폰은 최대한 보지 말고 걷는 것 자체에 집중해보세요.', 'HEALTH', 'MEDIUM', 20, 20, 'https://images.unsplash.com/photo-1597644568217-780bd0b0efb2', '편안한 운동화를 신으세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '집에서 30분 홈트레이닝', '유튜브 영상을 보며 30분 홈트레이닝 해보세요', '유튜브에서 초보자용 홈트레이닝 영상을 찾아서 30분 동안 따라해보세요.', 'HEALTH', 'MEDIUM', 25, 25, 'https://images.unsplash.com/photo-1597644568217-780bd0b0efb2', '운동 전후 스트레칭을 꼭 하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '5000보 걷기', '하루에 5000보를 걸어보세요', '만보기나 스마트폰 앱을 이용해서 하루에 5000보를 걷는 것을 목표로 해보세요.', 'HEALTH', 'MEDIUM', 20, 20, 'https://images.unsplash.com/photo-1597644568217-780bd0b0efb2', '한 번에 걷지 말고 여러 번 나누어 걸으세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '1시간 동안 운동하기', '헬스장, 요가, 달리기 중 원하는 운동을 선택해 1시간 해보세요', '헬스장에서 웨이트 트레이닝, 집에서 홈트레이닝, 공원에서 달리기, 요가원에서 요가 등 자신이 좋아하는 운동을 1시간 동안 해보세요.', 'HEALTH', 'HARD', 30, 30, 'https://images.unsplash.com/photo-1597644568217-780bd0b0efb2', '운동 전 충분히 스트레칭하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '10km 걷기 또는 5km 뛰기', '10km 걷기나 5km 뛰기 중 선택해서 도전해보세요', '한강공원이나 공원에서 10km를 걷거나 5km를 뛰어보세요. 자신의 체력에 맞게 선택하세요.', 'HEALTH', 'HARD', 40, 40, 'https://images.unsplash.com/photo-1597644568217-780bd0b0efb2', '충분한 준비운동을 하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '새벽 5시 기상 후 운동하기', '새벽 5시에 일어나서 1시간 운동하기', '새벽 5시에 일어나서 조깅, 홈트레이닝, 요가 등 1시간 동안 운동해보세요.', 'HEALTH', 'HARD', 40, 40, 'https://images.unsplash.com/photo-1597644568217-780bd0b0efb2', '전날 일찍 자야 해요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

-- CREATIVE 미션들 (10개)
(NULL, '사진으로 하루 기록하기', '오늘 하루를 사진 10장으로 기록해보세요', '아침부터 저녁까지, 일상 속 특별한 순간들을 사진으로 담아보세요. 음식, 풍경, 사람, 감정 등 무엇이든 좋습니다. 10장의 사진으로 오늘 하루를 이야기해보세요.', 'CREATIVE', 'EASY', 10, 10, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '특별하지 않은 순간도 특별하게 담아보세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '하루 일기 쓰기', '오늘 하루 있었던 일을 일기로 써보세요', '오늘 하루 있었던 일, 느낀 감정, 새로 알게 된 것들을 일기로 써보세요. 길지 않아도 괜찮습니다.', 'CREATIVE', 'EASY', 10, 10, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '솔직한 감정을 써보세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '스마트폰으로 짧은 영상 만들기', '1분 분량의 짧은 영상을 만들어보세요', '스마트폰으로 일상을 담은 1분짜리 영상을 만들어보세요. 편집 앱을 사용해도 좋습니다.', 'CREATIVE', 'EASY', 15, 15, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '스토리를 생각해보세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '손글씨로 좋아하는 문구 쓰기', '좋아하는 명언이나 문구를 예쁜 손글씨로 써보세요', '마음에 드는 명언, 가사, 시 등을 예쁜 손글씨로 써보세요. 꾸미기도 해보세요.', 'CREATIVE', 'EASY', 10, 10, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '차분한 마음으로 써보세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '일상 사물로 예술 작품 만들기', '주변에 있는 물건들로 창의적인 작품을 만들어보세요', '집에 있는 일상 용품들(펜, 책, 컵, 화분 등)을 이용해 나만의 작품을 만들어보세요. 조각, 설치 미술, 콜라주 등 어떤 형태든 상관없습니다.', 'CREATIVE', 'MEDIUM', 20, 20, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '완벽하지 않아도 괜찮아요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '포토북 만들기', '최근 사진들로 포토북이나 스크랩북을 만들어보세요', '스마트폰에 있는 사진들을 인화하거나 프린트해서 포토북이나 스크랩북을 만들어보세요.', 'CREATIVE', 'MEDIUM', 25, 25, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '테마를 정해보세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '요리 레시피 창작하기', '기존 요리를 응용해서 나만의 레시피를 만들어보세요', '좋아하는 요리를 베이스로 해서 재료를 바꾸거나 추가해서 나만의 새로운 레시피를 만들어보세요.', 'CREATIVE', 'MEDIUM', 25, 25, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '안전한 재료 조합을 선택하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '짧은 소설 한 편 쓰기', '1000자 내외의 짧은 소설을 써보세요', '오늘 겪은 일, 상상 속의 이야기, 또는 주변 사람들에서 영감을 받아 1000자 정도의 짧은 소설을 써보세요. 완벽하지 않아도 괜찮습니다.', 'CREATIVE', 'HARD', 30, 30, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '처음과 끝을 정하고 시작하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '하루 종일 사진 프로젝트', '하루 종일 특정 주제로 사진을 찍어보세요', '감정, 색깔, 모양 등 하나의 주제를 정해서 하루 종일 그 주제에 맞는 사진을 찍어보세요.', 'CREATIVE', 'HARD', 35, 35, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '명확한 주제를 정하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '나만의 브랜드 만들기', '가상의 브랜드를 만들고 로고와 컨셉을 디자인해보세요', '내가 만들고 싶은 브랜드(카페, 의류, 서비스 등)를 상상해서 브랜드명, 로고, 컨셉을 디자인해보세요.', 'CREATIVE', 'HARD', 45, 45, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '타겟을 명확히 하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

-- LEARNING 미션들 (10개)
(NULL, '새로운 단어 5개 배우기', '모르던 단어들을 찾아서 뜻을 익히고 문장을 만들어보세요', '책, 뉴스, 인터넷에서 모르는 단어 5개를 찾아 사전에서 뜻을 찾아보고, 각각을 사용해 문장을 만들어보세요. 오늘 대화에서 사용해보면 더 좋습니다.', 'LEARNING', 'EASY', 10, 10, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '실생활에서 쓸 수 있는 단어를 선택하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '유튜브로 5분 지식 배우기', '유튜브에서 5분짜리 교육 영상을 보고 새로운 지식을 배워보세요', 'TED-Ed, 지식채널e, 과학 유튜브 등에서 5분 내외의 교육 영상을 보고 새로운 지식을 학습해보세요.', 'LEARNING', 'EASY', 10, 10, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '관심 있는 분야를 선택하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '뉴스 기사 하나 완독하기', '평소에 관심 없던 분야의 뉴스 기사를 끝까지 읽어보세요', '정치, 경제, 과학, 문화 등 평소에 잘 읽지 않던 분야의 뉴스 기사를 하나 선택해서 끝까지 읽어보세요.', 'LEARNING', 'EASY', 10, 10, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '모르는 용어는 검색해보세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '타자 연습 10분 하기', '온라인 타자 연습 사이트에서 10분 동안 연습해보세요', '한글이나 영어 타자 연습 사이트에서 10분 동안 타자 연습을 해서 속도와 정확도를 향상시켜보세요.', 'LEARNING', 'EASY', 10, 10, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '정확도를 먼저 높이세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '새로운 요리 레시피 배우기', '30분 동안 새로운 요리 레시피를 도전해보세요', '유튜브나 요리책에서 만들어보지 않은 요리를 하나 골라 따라해보세요. 실패해도 괜찮으니 과정을 즐겨보세요.', 'LEARNING', 'MEDIUM', 20, 20, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '재료를 미리 준비하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '온라인 강의 1시간 듣기', '관심 있는 분야의 온라인 강의를 1시간 들어보세요', 'Coursera, 유데미, 인프런 등에서 관심 있는 분야의 무료 강의를 1시간 정도 들어보세요.', 'LEARNING', 'MEDIUM', 25, 25, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '노트를 준비하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '새로운 기술 튜토리얼 따라하기', '프로그래밍, 디자인 등의 튜토리얼을 따라해보세요', '유튜브나 온라인 자료에서 프로그래밍, 포토샵, 일러스트 등의 튜토리얼을 찾아서 따라해보세요.', 'LEARNING', 'MEDIUM', 30, 30, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '필요한 프로그램을 미리 설치하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '1시간 동안 새로운 기술 배우기', '온라인 강의나 튜토리얼을 통해 새로운 스킬을 익혀보세요', '유튜브, 온라인 강의 플랫폼에서 관심 있던 기술이나 스킬(포토샵, 프로그래밍, 외국어, 악기 등)을 1시간 동안 배워보세요.', 'LEARNING', 'HARD', 30, 30, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '기초부터 차근차근 시작하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '새로운 프로그래밍 언어 기초 익히기', '처음 접하는 프로그래밍 언어로 간단한 프로그램 만들기', 'Python, JavaScript, Java 등 처음 접하는 프로그래밍 언어를 선택해서 Hello World부터 시작해 간단한 계산기 프로그램까지 만들어보세요.', 'LEARNING', 'HARD', 40, 40, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '개발 환경을 먼저 설정하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW()),

(NULL, '온라인 과정 완주하기', '짧은 온라인 과정 하나를 완전히 완주해보세요', 'Coursera, edX, Khan Academy 등에서 1-2주 완성 과정을 선택해서 끝까지 완주하고 인증서를 받아보세요.', 'LEARNING', 'HARD', 50, 50, 'https://images.unsplash.com/photo-1549185545-f5b8a1fc481a', '완주 가능한 과정을 선택하세요', true, 'ASSIGNED', 0, 0, 0.0, 0, 0, 0.0, NOW(), NOW() + INTERVAL '1 day', NOW(), NOW());