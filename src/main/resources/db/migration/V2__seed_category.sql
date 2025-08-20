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

INSERT IGNORE INTO local_category (type, local_name) VALUES
-- Seoul
('Seoul', 'Gangnam-gu'), ('Seoul', 'Gangdong-gu'), ('Seoul', 'Gangbuk-gu'), ('Seoul', 'Gangseo-gu'), ('Seoul', 'Gwanak-gu'), ('Seoul', 'Gwangjin-gu'), ('Seoul', 'Guro-gu'), ('Seoul', 'Geumcheon-gu'), ('Seoul', 'Nowon-gu'), ('Seoul', 'Dobong-gu'), ('Seoul', 'Dongdaemun-gu'), ('Seoul', 'Dongjak-gu'), ('Seoul', 'Mapo-gu'), ('Seoul', 'Seodaemun-gu'), ('Seoul', 'Seocho-gu'), ('Seoul', 'Seongdong-gu'), ('Seoul', 'Seongbuk-gu'), ('Seoul', 'Songpa-gu'), ('Seoul', 'Yangcheon-gu'), ('Seoul', 'Yeongdeungpo-gu'), ('Seoul', 'Yongsan-gu'), ('Seoul', 'Eunpyeong-gu'), ('Seoul', 'Jongno-gu'), ('Seoul', 'Jung-gu'), ('Seoul', 'Jungnang-gu'),
-- Gyeonggi
('Gyeonggi', 'Gapyeong-gun'), ('Gyeonggi', 'Goyang-si'), ('Gyeonggi', 'Gwacheon-si'), ('Gyeonggi', 'Gwangmyeong-si'), ('Gyeonggi', 'Gwangju-si'), ('Gyeonggi', 'Guri-si'), ('Gyeonggi', 'Gunpo-si'), ('Gyeonggi', 'Gimpo-si'), ('Gyeonggi', 'Namyangju-si'), ('Gyeonggi', 'Dongducheon-si'), ('Gyeonggi', 'Bucheon-si'), ('Gyeonggi', 'Seongnam-si'), ('Gyeonggi', 'Suwon-si'), ('Gyeonggi', 'Siheung-si'), ('Gyeonggi', 'Ansan-si'), ('Gyeonggi', 'Anseong-si'), ('Gyeonggi', 'Anyang-si'), ('Gyeonggi', 'Yangju-si'), ('Gyeonggi', 'Yangpyeong-gun'), ('Gyeonggi', 'Yeoju-si'), ('Gyeonggi', 'Yeoncheon-gun'), ('Gyeonggi', 'Osan-si'), ('Gyeonggi', 'Yongin-si'), ('Gyeonggi', 'Uiwang-si'), ('Gyeonggi', 'Uijeongbu-si'), ('Gyeonggi', 'Icheon-si'), ('Gyeonggi', 'Paju-si'), ('Gyeonggi', 'Pocheon-si'), ('Gyeonggi', 'Pyeongtaek-si'), ('Gyeonggi', 'Hanam-si'), ('Gyeonggi', 'Hwaseong-si'),
-- Incheon
('Incheon', 'Ganghwa-gun'), ('Incheon', 'Gyeyang-gu'), ('Incheon', 'Namdong-gu'), ('Incheon', 'Dong-gu'), ('Incheon', 'Michuhol-gu'), ('Incheon', 'Bupyeong-gu'), ('Incheon', 'Seo-gu'), ('Incheon', 'Yeonsu-gu'), ('Incheon', 'Ongjin-gun'), ('Incheon', 'Jung-gu'),
-- Gangwon
('Gangwon', 'Gangneung-si'), ('Gangwon', 'Goseong-gun'), ('Gangwon', 'Donghae-si'), ('Gangwon', 'Samcheok-si'), ('Gangwon', 'Sokcho-si'), ('Gangwon', 'Yanggu-gun'), ('Gangwon', 'Yangyang-gun'), ('Gangwon', 'Yeongwol-gun'), ('Gangwon', 'Wonju-si'), ('Gangwon', 'Inje-gun'), ('Gangwon', 'Jeongseon-gun'), ('Gangwon', 'Chuncheon-si'), ('Gangwon', 'Cheorwon-gun'), ('Gangwon', 'Taebaek-si'), ('Gangwon', 'Pyeongchang-gun'), ('Gangwon', 'Hoengseong-gun'), ('Gangwon', 'Hongcheon-gun'), ('Gangwon', 'Hwacheon-gun'),
-- Daejeon
('Daejeon', 'Daedeok-gu'), ('Daejeon', 'Dong-gu'), ('Daejeon', 'Seo-gu'), ('Daejeon', 'Yuseong-gu'), ('Daejeon', 'Jung-gu'),
-- Sejong
('Sejong', 'Sejong-si'),
-- Chungnam
('Chungnam', 'Gyeryong-si'), ('Chungnam', 'Gongju-si'), ('Chungnam', 'Geumsan-gun'), ('Chungnam', 'Nonsan-si'), ('Chungnam', 'Dangjin-si'), ('Chungnam', 'Boryeong-si'), ('Chungnam', 'Buyeo-gun'), ('Chungnam', 'Seosan-si'), ('Chungnam', 'Seocheon-gun'), ('Chungnam', 'Asan-si'), ('Chungnam', 'Yesan-gun'), ('Chungnam', 'Cheonan-si'), ('Chungnam', 'Cheongyang-gun'), ('Chungnam', 'Taean-gun'), ('Chungnam', 'Hongseong-gun'),
-- Chungbuk
('Chungbuk', 'Goesan-gun'), ('Chungbuk', 'Danyang-gun'), ('Chungbuk', 'Boeun-gun'), ('Chungbuk', 'Yeongdong-gun'), ('Chungbuk', 'Okcheon-gun'), ('Chungbuk', 'Eumseong-gun'), ('Chungbuk', 'Jeungpyeong-gun'), ('Chungbuk', 'Jincheon-gun'), ('Chungbuk', 'Jecheon-si'), ('Chungbuk', 'Cheongju-si'), ('Chungbuk', 'Chungju-si'),
-- Busan
('Busan', 'Gangseo-gu'), ('Busan', 'Geumjeong-gu'), ('Busan', 'Gijang-gun'), ('Busan', 'Nam-gu'), ('Busan', 'Dong-gu'), ('Busan', 'Dongnae-gu'), ('Busan', 'Busanjin-gu'), ('Busan', 'Buk-gu'), ('Busan', 'Sasang-gu'), ('Busan', 'Saha-gu'), ('Busan', 'Seo-gu'), ('Busan', 'Suyeong-gu'), ('Busan', 'Yeongdo-gu'), ('Busan', 'Yeonje-gu'), ('Busan', 'Jung-gu'), ('Busan', 'Haeundae-gu'),
-- Ulsan
('Ulsan', 'Nam-gu'), ('Ulsan', 'Dong-gu'), ('Ulsan', 'Buk-gu'), ('Ulsan', 'Ulju-gun'), ('Ulsan', 'Jung-gu'),
-- Gyeongnam
('Gyeongnam', 'Geoje-si'), ('Gyeongnam', 'Geochang-gun'), ('Gyeongnam', 'Goseong-gun'), ('Gyeongnam', 'Gimhae-si'), ('Gyeongnam', 'Namhae-gun'), ('Gyeongnam', 'Miryang-si'), ('Gyeongnam', 'Sacheon-si'), ('Gyeongnam', 'Sancheong-gun'), ('Gyeongnam', 'Yangsan-si'), ('Gyeongnam', 'Uiryeong-gun'), ('Gyeongnam', 'Jinju-si'), ('Gyeongnam', 'Changnyeong-gun'), ('Gyeongnam', 'Changwon-si'), ('Gyeongnam', 'Tongyeong-si'), ('Gyeongnam', 'Hadong-gun'), ('Gyeongnam', 'Haman-gun'), ('Gyeongnam', 'Hamyang-gun'), ('Gyeongnam', 'Hapcheon-gun'),
-- Gyeongbuk
('Gyeongbuk', 'Gyeongsan-si'), ('Gyeongbuk', 'Gyeongju-si'), ('Gyeongbuk', 'Goryeong-gun'), ('Gyeongbuk', 'Gumi-si'), ('Gyeongbuk', 'Gimcheon-si'), ('Gyeongbuk', 'Mungyeong-si'), ('Gyeongbuk', 'Bonghwa-gun'), ('Gyeongbuk', 'Sangju-si'), ('Gyeongbuk', 'Seongju-gun'), ('Gyeongbuk', 'Andong-si'), ('Gyeongbuk', 'Yeongdeok-gun'), ('Gyeongbuk', 'Yeongyang-gun'), ('Gyeongbuk', 'Yeongju-si'), ('Gyeongbuk', 'Yeongcheon-si'), ('Gyeongbuk', 'Yecheon-gun'), ('Gyeongbuk', 'Ulleung-gun'), ('Gyeongbuk', 'Uljin-gun'), ('Gyeongbuk', 'Uiseong-gun'), ('Gyeongbuk', 'Cheongdo-gun'), ('Gyeongbuk', 'Cheongsong-gun'), ('Gyeongbuk', 'Chilgok-gun'), ('Gyeongbuk', 'Pohang-si'),
-- Daegu
('Daegu', 'Gunwi-gun'), ('Daegu', 'Nam-gu'), ('Daegu', 'Dalseo-gu'), ('Daegu', 'Dalseong-gun'), ('Daegu', 'Dong-gu'), ('Daegu', 'Buk-gu'), ('Daegu', 'Seo-gu'), ('Daegu', 'Suseong-gu'), ('Daegu', 'Jung-gu'),
-- Gwangju
('Gwangju', 'Gwangsan-gu'), ('Gwangju', 'Nam-gu'), ('Gwangju', 'Dong-gu'), ('Gwangju', 'Buk-gu'), ('Gwangju', 'Seo-gu'),
-- Jeonnam
('Jeonnam', 'Gangjin-gun'), ('Jeonnam', 'Goheung-gun'), ('Jeonnam', 'Gokseong-gun'), ('Jeonnam', 'Gwangyang-si'), ('Jeonnam', 'Gurye-gun'), ('Jeonnam', 'Naju-si'), ('Jeonnam', 'Damyang-gun'), ('Jeonnam', 'Mokpo-si'), ('Jeonnam', 'Muan-gun'), ('Jeonnam', 'Boseong-gun'), ('Jeonnam', 'Suncheon-si'), ('Jeonnam', 'Shinan-gun'), ('Jeonnam', 'Yeosu-si'), ('Jeonnam', 'Yeonggwang-gun'), ('Jeonnam', 'Yeongam-gun'), ('Jeonnam', 'Wando-gun'), ('Jeonnam', 'Jangseong-gun'), ('Jeonnam', 'Jangheung-gun'), ('Jeonnam', 'Jindo-gun'), ('Jeonnam', 'Hampyeong-gun'), ('Jeonnam', 'Haenam-gun'), ('Jeonnam', 'Hwasun-gun'),
-- Jeonbuk
('Jeonbuk', 'Gochang-gun'), ('Jeonbuk', 'Gunsan-si'), ('Jeonbuk', 'Gimje-si'), ('Jeonbuk', 'Namwon-si'), ('Jeonbuk', 'Muju-gun'), ('Jeonbuk', 'Buan-gun'), ('Jeonbuk', 'Sunchang-gun'), ('Jeonbuk', 'Wanju-gun'), ('Jeonbuk', 'Iksan-si'), ('Jeonbuk', 'Imsil-gun'), ('Jeonbuk', 'Jangsu-gun'), ('Jeonbuk', 'Jeonju-si'), ('Jeonbuk', 'Jeongeup-si'), ('Jeonbuk', 'Jinan-gun'),
-- Jeju
('Jeju', 'Seogwipo-si'), ('Jeju', 'Jeju-si');