-- OAuth Providers
INSERT INTO oauth_providers (name) VALUES
('google'),
('kakao')
ON CONFLICT (name) DO NOTHING;
-- Category - mood
INSERT INTO category (type, name) VALUES
('moods', 'TRADITIONAL'),
('moods', 'UNIQUE'),
('moods', 'EMOTIONAL'),
('moods', 'HEALING'),
('moods', 'GOODVIEW'),
('moods', 'ACTIVITY'),      -- 활기찬
('moods', 'LOCAL')      -- 로컬
ON CONFLICT (name) DO NOTHING;

-- Category - food
INSERT INTO category (type, name) VALUES
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
('foods', 'JJIM_TANG')      -- 찜 / 탕
ON CONFLICT (name) DO NOTHING;

-- Category - companion (같이하는 동반자)
INSERT INTO category (type, name) VALUES
('companions', 'ALONE'),        -- 혼자
('companions', 'FRIEND'),       -- 친구
('companions', 'COUPLE'),       -- 연인
('companions', 'FAMILY'),       -- 가족
('companions', 'GROUP'),        -- 단체
('companions', 'WITH_PET'),     -- 반려동물과 함께
('companions', 'ALUMNI')      -- 동호회
ON CONFLICT (name) DO NOTHING;

INSERT INTO local_category (type, local_name, area_code, sigungu_code) VALUES
-- Seoul
('Seoul', 'Jongno-gu', 11, 11110), ('Seoul', 'Jung-gu', 11, 11140), ('Seoul', 'Yongsan-gu', 11, 11170), ('Seoul', 'Seongdong-gu', 11, 11200), ('Seoul', 'Gwangjin-gu', 11, 11215), ('Seoul', 'Dongdaemun-gu', 11, 11230), ('Seoul', 'Jungnang-gu', 11, 11260), ('Seoul', 'Seongbuk-gu', 11, 11290), ('Seoul', 'Gangbuk-gu', 11, 11305), ('Seoul', 'Dobong-gu', 11, 11320), ('Seoul', 'Nowon-gu', 11, 11350), ('Seoul', 'Eunpyeong-gu', 11, 11380), ('Seoul', 'Seodaemun-gu', 11, 11410),
('Seoul', 'Mapo-gu', 11, 11440), ('Seoul', 'Yangcheon-gu', 11, 11470), ('Seoul', 'Gangseo-gu', 11, 11500), ('Seoul', 'Guro-gu', 11, 11530), ('Seoul', 'Geumcheon-gu', 11, 11545), ('Seoul', 'Yeongdeungpo-gu', 11, 11560), ('Seoul', 'Dongjak-gu', 11, 11590), ('Seoul', 'Gwanak-gu', 11, 11620), ('Seoul', 'Seocho-gu', 11, 11650), ('Seoul', 'Gangnam-gu', 11, 11680), ('Seoul', 'Songpa-gu', 11, 11710), ('Seoul', 'Gangdong-gu', 11, 11740),
-- Busan
('Busan', 'Jung-gu', 26, 26110), ('Busan', 'Seo-gu', 26, 26140), ('Busan', 'Dong-gu', 26, 26170), ('Busan', 'Yeongdo-gu', 26, 26200), ('Busan', 'Busanjin-gu', 26, 26230), ('Busan', 'Dongnae-gu', 26, 26260), ('Busan', 'Nam-gu', 26, 26290), ('Busan', 'Buk-gu', 26, 26320), ('Busan', 'Haeundae-gu', 26, 26350), ('Busan', 'Saha-gu', 26, 26380), ('Busan', 'Geumjeong-gu', 26, 26410), ('Busan', 'Gangseo-gu', 26, 26440), ('Busan', 'Yeonje-gu', 26, 26470), ('Busan', 'Suyeong-gu', 26, 26500), ('Busan', 'Sasang-gu', 26, 26530), ('Busan', 'Gijang-gun', 26, 26710),
-- Daegu
('Daegu', 'Jung-gu', 27, 27110), ('Daegu', 'Dong-gu', 27, 27140), ('Daegu', 'Seo-gu', 27, 27170), ('Daegu', 'Nam-gu', 27, 27200), ('Daegu', 'Buk-gu', 27, 27230), ('Daegu', 'Suseong-gu', 27, 27260), ('Daegu', 'Dalseo-gu', 27, 27290), ('Daegu', 'Dalseong-gun', 27, 27710), ('Daegu', 'Gunwi-gun', 27, 27720),
-- Incheon
('Incheon', 'Jung-gu', 28, 28110), ('Incheon', 'Dong-gu', 28, 28140), ('Incheon', 'Michuhol-gu', 28, 28177), ('Incheon', 'Yeonsu-gu', 28, 28185), ('Incheon', 'Namdong-gu', 28, 28200), ('Incheon', 'Bupyeong-gu', 28, 28237), ('Incheon', 'Gyeyang-gu', 28, 28245), ('Incheon', 'Seo-gu', 28, 28260), ('Incheon', 'Ganghwa-gun', 28, 28710), ('Incheon', 'Ongjin-gun', 28, 28720),
-- Gwangju
('Gwangju', 'Dong-gu', 29, 29110), ('Gwangju', 'Seo-gu', 29, 29140), ('Gwangju', 'Nam-gu', 29, 29155), ('Gwangju', 'Buk-gu', 29, 29170), ('Gwangju', 'Gwangsan-gu', 29, 29200),
-- Daejeon
('Daejeon', 'Dong-gu', 30, 30110), ('Daejeon', 'Jung-gu', 30, 30140), ('Daejeon', 'Seo-gu', 30, 30170), ('Daejeon', 'Yuseong-gu', 30, 30200), ('Daejeon', 'Daedeok-gu', 30, 30230),
-- Ulsan
('Ulsan', 'Jung-gu', 31, 31110), ('Ulsan', 'Nam-gu', 31, 31140), ('Ulsan', 'Dong-gu', 31, 31170), ('Ulsan', 'Buk-gu', 31, 31200), ('Ulsan', 'Ulju-gun', 31, 31710),
-- Sejong
('Sejong', 'Sejong-si', 36, 36110),
-- Gyeonggi
('Gyeonggi', 'Suwon-si', 41, 41111), ('Gyeonggi', 'Suwon-si', 41, 41113), ('Gyeonggi', 'Suwon-si', 41, 41115), ('Gyeonggi', 'Suwon-si', 41, 41117), ('Gyeonggi', 'Seongnam-si', 41, 41131), ('Gyeonggi', 'Seongnam-si', 41, 41133), ('Gyeonggi', 'Seongnam-si', 41, 41135), ('Gyeonggi', 'Uijeongbu-si', 41, 41150), ('Gyeonggi', 'Anyang-si', 41, 41171), ('Gyeonggi', 'Anyang-si', 41, 41173), ('Gyeonggi', 'Bucheon-si', 41, 41192), ('Gyeonggi', 'Bucheon-si', 41, 41194), ('Gyeonggi', 'Bucheon-si', 41, 41196), ('Gyeonggi', 'Gwangmyeong-si', 41, 41210), ('Gyeonggi', 'Pyeongtaek-si', 41, 41220), ('Gyeonggi', 'Dongducheon-si', 41, 41250), ('Gyeonggi', 'Ansan-si', 41, 41271), ('Gyeonggi', 'Ansan-si', 41, 41273), ('Gyeonggi', 'Goyang-si', 41, 41281), ('Gyeonggi', 'Goyang-si', 41, 41285), ('Gyeonggi', 'Goyang-si', 41, 41287), ('Gyeonggi', 'Gwacheon-si', 41, 41290),
 ('Gyeonggi', 'Guri-si', 41, 41310), ('Gyeonggi', 'Namyangju-si', 41, 41360), ('Gyeonggi', 'Osan-si', 41, 41370), ('Gyeonggi', 'Siheung-si', 41, 41390), ('Gyeonggi', 'Gunpo-si', 41, 41410), ('Gyeonggi', 'Uiwang-si', 41, 41430), ('Gyeonggi', 'Hanam-si', 41, 41450), ('Gyeonggi', 'Yongin-si', 41, 41461), ('Gyeonggi', 'Yongin-si', 41, 41463), ('Gyeonggi', 'Yongin-si', 41, 41465), ('Gyeonggi', 'Paju-si', 41, 41480), ('Gyeonggi', 'Icheon-si', 41, 41500), ('Gyeonggi', 'Anseong-si', 41, 41550), ('Gyeonggi', 'Gimpo-si', 41, 41570), ('Gyeonggi', 'Hwaseong-si', 41, 41590), ('Gyeonggi', 'Gwangju-si', 41, 41610), ('Gyeonggi', 'Yangju-si', 41, 41630), ('Gyeonggi', 'Pocheon-si', 41, 41650), ('Gyeonggi', 'Yeoju-si', 41, 41670), ('Gyeonggi', 'Yeoncheon-gun', 41, 41800), ('Gyeonggi', 'Gapyeong-gun', 41, 41820), ('Gyeonggi', 'Yangpyeong-gun', 41, 41830),
-- Chungbuk
('Chungbuk', 'Cheongju-si', 43, 43111), ('Chungbuk', 'Cheongju-si', 43, 43112), ('Chungbuk', 'Cheongju-si', 43, 43113), ('Chungbuk', 'Cheongju-si', 43, 43114), ('Chungbuk', 'Chungju-si', 43, 43130), ('Chungbuk', 'Jecheon-si', 43, 43150), ('Chungbuk', 'Boeun-gun', 43, 43720), ('Chungbuk', 'Okcheon-gun', 43, 43730), ('Chungbuk', 'Yeongdong-gun', 43, 43740), ('Chungbuk', 'Jeungpyeong-gun', 43, 43745), ('Chungbuk', 'Jincheon-gun', 43, 43750), ('Chungbuk', 'Goesan-gun', 43, 43760), ('Chungbuk', 'Eumseong-gun', 43, 43770), ('Chungbuk', 'Danyang-gun', 43, 43800),
-- Chungnam
('Chungnam', 'Cheonan-si', 44, 44131), ('Chungnam', 'Cheonan-si', 44, 44133), ('Chungnam', 'Gongju-si', 44, 44150), ('Chungnam', 'Boryeong-si', 44, 44180), ('Chungnam', 'Asan-si', 44, 44200), ('Chungnam', 'Seosan-si', 44, 44210), ('Chungnam', 'Nonsan-si', 44, 44230), ('Chungnam', 'Gyeryong-si', 44, 44250), ('Chungnam', 'Dangjin-si', 44, 44270), ('Chungnam', 'Geumsan-gun', 44, 44710), ('Chungnam', 'Buyeo-gun', 44, 44760), ('Chungnam', 'Seocheon-gun', 44, 44770), ('Chungnam', 'Cheongyang-gun', 44, 44790), ('Chungnam', 'Hongseong-gun', 44, 44800), ('Chungnam', 'Yesan-gun', 44, 44810), ('Chungnam', 'Taean-gun', 44, 44825),
-- Jeonnam
('Jeonnam', 'Mokpo-si', 46, 46110), ('Jeonnam', 'Yeosu-si', 46, 46130), ('Jeonnam', 'Suncheon-si', 46, 46150), ('Jeonnam', 'Naju-si', 46, 46170), ('Jeonnam', 'Gwangyang-si', 46, 46230), ('Jeonnam', 'Damyang-gun', 46, 46710), ('Jeonnam', 'Gokseong-gun', 46, 46720), ('Jeonnam', 'Gurye-gun', 46, 46730), ('Jeonnam', 'Goheung-gun', 46, 46770), ('Jeonnam', 'Boseong-gun', 46, 46780), ('Jeonnam', 'Hwasun-gun', 46, 46790), ('Jeonnam', 'Jangheung-gun', 46, 46800), ('Jeonnam', 'Gangjin-gun', 46, 46810), ('Jeonnam', 'Haenam-gun', 46, 46820), ('Jeonnam', 'Yeongam-gun', 46, 46830), ('Jeonnam', 'Muan-gun', 46, 46840), ('Jeonnam', 'Hampyeong-gun', 46, 46860), ('Jeonnam', 'Yeonggwang-gun', 46, 46870), ('Jeonnam', 'Jangseong-gun', 46, 46880), ('Jeonnam', 'Wando-gun', 46, 46890), ('Jeonnam', 'Jindo-gun', 46, 46900), ('Jeonnam', 'Shinan-gun', 46, 46910),
-- Gyeongbuk
('Gyeongbuk', 'Pohang-si', 47, 47111), ('Gyeongbuk', 'Pohang-si', 47, 47113), ('Gyeongbuk', 'Gyeongju-si', 47, 47130), ('Gyeongbuk', 'Gimcheon-si', 47, 47150), ('Gyeongbuk', 'Andong-si', 47, 47170), ('Gyeongbuk', 'Gumi-si', 47, 47190), ('Gyeongbuk', 'Yeongju-si', 47, 47210), ('Gyeongbuk', 'Yeongcheon-si', 47, 47230), ('Gyeongbuk', 'Sangju-si', 47, 47250), ('Gyeongbuk', 'Mungyeong-si', 47, 47280), ('Gyeongbuk', 'Gyeongsan-si', 47, 47290), ('Gyeongbuk', 'Uiseong-gun', 47, 47730), ('Gyeongbuk', 'Cheongsong-gun', 47, 47750), ('Gyeongbuk', 'Yeongyang-gun', 47, 47760), ('Gyeongbuk', 'Yeongdeok-gun', 47, 47770), ('Gyeongbuk', 'Cheongdo-gun', 47, 47820), ('Gyeongbuk', 'Goryeong-gun', 47, 47830), ('Gyeongbuk', 'Seongju-gun', 47, 47840), ('Gyeongbuk', 'Chilgok-gun', 47, 47850), ('Gyeongbuk', 'Yecheon-gun', 47, 47900), ('Gyeongbuk', 'Bonghwa-gun', 47, 47920), ('Gyeongbuk', 'Uljin-gun', 47, 47930), ('Gyeongbuk', 'Ulleung-gun', 47, 47940),
-- Gyeongnam
('Gyeongnam', 'Changwon-si', 48, 48121), ('Gyeongnam', 'Changwon-si', 48, 48123), ('Gyeongnam', 'Changwon-si', 48, 48125), ('Gyeongnam', 'Changwon-si', 48, 48127), ('Gyeongnam', 'Changwon-si', 48, 48129), ('Gyeongnam', 'Jinju-si', 48, 48170), ('Gyeongnam', 'Tongyeong-si', 48, 48220), ('Gyeongnam', 'Sacheon-si', 48, 48240), ('Gyeongnam', 'Gimhae-si', 48, 48250), ('Gyeongnam', 'Miryang-si', 48, 48270), ('Gyeongnam', 'Geoje-si', 48, 48310), ('Gyeongnam', 'Yangsan-si', 48, 48330), ('Gyeongnam', 'Uiryeong-gun', 48, 48720), ('Gyeongnam', 'Haman-gun', 48, 48730), ('Gyeongnam', 'Changnyeong-gun', 48, 48740), ('Gyeongnam', 'Goseong-gun', 48, 48820), ('Gyeongnam', 'Namhae-gun', 48, 48840), ('Gyeongnam', 'Hadong-gun', 48, 48850), ('Gyeongnam', 'Sancheong-gun', 48, 48860), ('Gyeongnam', 'Hamyang-gun', 48, 48870), ('Gyeongnam', 'Geochang-gun', 48, 48880), ('Gyeongnam', 'Hapcheon-gun', 48, 48890),
-- Jeju
('Jeju', 'Jeju-si', 50, 50110), ('Jeju', 'Seogwipo-si', 50, 50130),
-- Gangwon
('Gangwon', 'Chuncheon-si', 51, 51110), ('Gangwon', 'Wonju-si', 51, 51130), ('Gangwon', 'Gangneung-si', 51, 51150), ('Gangwon', 'Donghae-si', 51, 51170), ('Gangwon', 'Taebaek-si', 51, 51190), ('Gangwon', 'Sokcho-si', 51, 51210), ('Gangwon', 'Samcheok-si', 51, 51230), ('Gangwon', 'Hongcheon-gun', 51, 51720), ('Gangwon', 'Hoengseong-gun', 51, 51730), ('Gangwon', 'Yeongwol-gun', 51, 51750), ('Gangwon', 'Pyeongchang-gun', 51, 51760), ('Gangwon', 'Jeongseon-gun', 51, 51770), ('Gangwon', 'Cheorwon-gun', 51, 51780), ('Gangwon', 'Hwacheon-gun', 51, 51790), ('Gangwon', 'Yanggu-gun', 51, 51800), ('Gangwon', 'Inje-gun', 51, 51810), ('Gangwon', 'Goseong-gun', 51, 51820), ('Gangwon', 'Yangyang-gun', 51, 51830),
-- Jeonbuk
('Jeonbuk', 'Jeonju-si', 52, 52111), ('Jeonbuk', 'Jeonju-si', 52, 52113), ('Jeonbuk', 'Gunsan-si', 52, 52130), ('Jeonbuk', 'Iksan-si', 52, 52140), ('Jeonbuk', 'Jeongeup-si', 52, 52180), ('Jeonbuk', 'Namwon-si', 52, 52190), ('Jeonbuk', 'Gimje-si', 52, 52210), ('Jeonbuk', 'Wanju-gun', 52, 52710), ('Jeonbuk', 'Jinan-gun', 52, 52720), ('Jeonbuk', 'Muju-gun', 52, 52730), ('Jeonbuk', 'Jangsu-gun', 52, 52740), ('Jeonbuk', 'Imsil-gun', 52, 52750), ('Jeonbuk', 'Sunchang-gun', 52, 52770), ('Jeonbuk', 'Gochang-gun', 52, 52790), ('Jeonbuk', 'Buan-gun', 52, 52800)
ON CONFLICT (local_name) DO NOTHING;
