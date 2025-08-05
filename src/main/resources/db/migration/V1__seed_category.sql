-- OAuth Providers
INSERT IGNORE INTO oauth_providers (name) VALUES
('google'),
('kakao');

-- Category - mood
INSERT IGNORE INTO category (type, name) VALUES
('mood', 'TRADITIONAL'),
('mood', 'UNIQUE'),
('mood', 'EMOTIONAL'),
('mood', 'HEALING'),
('mood', 'GOODVIEW'),
('mood', 'ACTIVITY'),
('mood', 'ROMANTIC');

-- Category - food
INSERT IGNORE INTO category (type, name) VALUES
('food', 'BUNSIK'),
('food', 'CAFE_DESSERT'),
('food', 'CHICKEN'),
('food', 'CHINESE'),
('food', 'KOREAN'),
('food', 'PORKBBQ_SASHIMI'),
('food', 'FASTFOOD'),
('food', 'JOKBAL_BOSSAM');
