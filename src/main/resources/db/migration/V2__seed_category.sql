-- OAuth Providers
INSERT IGNORE INTO oauth_providers (name) VALUES
('google'),
('kakao');

-- Category - mood
INSERT IGNORE INTO category (type, name) VALUES
('moods', 'TRADITIONAL'),
('moods', 'UNIQUE'),
('moods', 'EMOTIONAL'),
('moods', 'HEALING'),
('moods', 'GOODVIEW'),
('moods', 'ACTIVITY'),      -- 활기찬
('moods', 'LOCAL');         -- 로컬

-- Category - food
INSERT IGNORE INTO category (type, name) VALUES
('foods', 'BUNSIK'),          -- 분식
('foods', 'CAFE_DESSERT'),    -- 카페 / 디저트
('foods', 'CHICKEN'),         -- 치킨
('foods', 'CHINESE'),         -- 중식
('foods', 'KOREAN'),          -- 한식
('foods', 'PORK_SASHIMI'),    -- 돈까스 / 회
('foods', 'FASTFOOD'),        -- 패스트푸드
('foods', 'JOKBAL_BOSSAM'),   -- 족발 / 보쌈
('foods', 'PIZZA'),           -- 피자
('foods', 'WESTERN'),         -- 양식
('foods', 'MEAT'),            -- 고기
('foods', 'ASIAN'),           -- 아시안
('foods', 'DOSIRAK'),         -- 도시락
('foods', 'LATE_NIGHT'),      -- 야식
('foods', 'JJIM_TANG');       -- 찜 / 탕

-- Category - companion (같이하는 동반자)
INSERT IGNORE INTO category (type, name) VALUES
('companions', 'ALONE'),        -- 혼자
('companions', 'FRIEND'),       -- 친구
('companions', 'COUPLE'),       -- 연인
('companions', 'FAMILY'),       -- 가족
('companions', 'GROUP'),        -- 단체
('companions', 'WITH_PET'),     -- 반려동물과 함께
('companions', 'ALUMNI');       -- 동호회