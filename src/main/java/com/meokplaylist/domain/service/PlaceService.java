package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.KakaoSearchResponse;
import com.meokplaylist.domain.repository.place.PlacesRepository;
import com.meokplaylist.domain.repository.feed.FeedPhotosRepository;
import com.meokplaylist.domain.repository.feed.FeedRepository;
import com.meokplaylist.domain.repository.roadmap.RoadMapPlaceRepository;
import com.meokplaylist.security.KakaoLocalClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final KakaoLocalClient kakao;

    private static final int PAGE_SIZE = 5; // 한 번에 요청할 size (1~15)

    //FD6:음식점, CE7:카페
    private static final String RESTAURANT ="FD6";
    private static final String CAFE ="CE7";

    private final FeedRepository feedRepository;
    private final FeedPhotosRepository feedPhotosRepository;
    private final PlacesRepository placesRepository;
    private final RoadMapPlaceRepository roadMapPlaceRepository;
    private final S3Service s3Service;


//    @Transactional
//    public  Map<Integer, List<KakaoSearchResponse.Document>> pullOutKakaoPlace(Long feedId){
//
//        Feed feed=feedRepository.findByFeedId(feedId)
//                .orElseThrow(()-> new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));
//
//        List<FeedPhotos> feedPhotos=feedPhotosRepository.findAllByFeedFeedIdOrderBySequenceAsc(feed.getFeedId());
//
//        Map<Integer, List<KakaoSearchResponse.Document>> KakoPlaceInfor = Map.of();
//
//        for(FeedPhotos fp : feedPhotos){
//            Double latitude=fp.getLatitude();
//            Double longitude=fp.getLongitude();
//
//            List<KakaoSearchResponse.Document> roadMapPlaceList = findAllPlaceByCategory(RESTAURANT,latitude,longitude);
//            KakoPlaceInfor.put(fp.getSequence(),roadMapPlaceList);
//        }
//
//        return KakoPlaceInfor;
//
//    }

    public List<KakaoSearchResponse.Document> findAllPlaceByCategory(double lat, double lng) {
        List<KakaoSearchResponse.Document> all = new ArrayList<>();
        Set<String> seenIds = new HashSet<>(); // 중복 방지용
        List<String> categories = List.of("FD6", "CE7");
        for (String category : categories) {
            for (int page = 1; page <= 3; page++) {
                KakaoSearchResponse res = kakao.searchByCategory(category, lng, lat, page, PAGE_SIZE);
                if (res == null || res.documents() == null || res.documents().isEmpty()) break;

                for (KakaoSearchResponse.Document doc : res.documents()) {
                    if (seenIds.add(doc.id())) {
                        all.add(doc);
                    }
                }

                // 끝 페이지면 종료
                if (res.meta() != null && res.meta().isEnd()) break;

                // 방어: 마지막 페이지가 size 미만이면 더 없음
                if (res.documents().size() < PAGE_SIZE) break;
            }
        }

        return all;
    }

    public KakaoSearchResponse.Document findPlaceByCategory(double lat, double lng) {
        // 카테고리: 음식점(FD6), 카페(CE7)
        List<String> categories = List.of("FD6", "CE7");

        for (String category : categories) {
            // 1페이지(가장 가까운 결과)만 조회
            KakaoSearchResponse res = kakao.searchByCategory(category, lng, lat, 1, 1);
            if (res != null && res.documents() != null && !res.documents().isEmpty()) {
                // 1개만 가져오므로 바로 리턴
                return res.documents().get(0);
            }
        }

        // 음식점/카페 아무것도 없으면 null
        return null;
    }
}
