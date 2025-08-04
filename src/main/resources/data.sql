INSERT INTO oauth_providers (name) VALUES ('google');
INSERT INTO oauth_providers (name) VALUES ('kakao');

-- 분위기 (mood)
INSERT INTO category (type, foodName) VALUES
('mood', 'TRADITIONAL'),
('mood', 'UNIQUE'),
('mood', 'EMOTIONAL'),
('mood', 'HEALING'),
('mood', 'GOODVIEW'),
('mood', 'ACTIVITY'),
('mood', 'ROMANTIC');

-- 음식 (food)
INSERT INTO category (type, foodName) VALUES
('food', 'BUNSIK'),
('food', 'CAFE_DESSERT'),
('food', 'CHICKEN'),
('food', 'CHINESE'),
('food', 'KOREAN'),
('food', 'PORKBBQ_SASHIMI'),
('food', 'FASTFOOD'),
('food', 'JOKBAL_BOSSAM'),
('food', 'PIZZA'),
('food', 'WESTERN'),
('food', 'MEAT'),
('food', 'ASIAN'),
('food', 'DOSIRAK'),
('food', 'LATE_NIGHT'),
('food', 'STEW_SOUP');

-- 동반자 (companion)
INSERT INTO category (type, foodName) VALUES
('companion', 'ALONE'),
('companion', 'FRIEND'),
('companion', 'LOVER'),
('companion', 'FAMILY'),
('companion', 'GROUP'),
('companion', 'WITH_PET'),
('companion', 'WITH_COLLEAGUE');

