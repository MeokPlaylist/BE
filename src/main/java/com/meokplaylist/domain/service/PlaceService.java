package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.CallInRoadMapDto;
import com.meokplaylist.api.dto.KakaoSearchResponse;
import com.meokplaylist.api.dto.SaveRoadMapPlaceRequest;
import com.meokplaylist.domain.repository.place.PlacesRepository;
import com.meokplaylist.domain.repository.feed.FeedPhotosRepository;
import com.meokplaylist.domain.repository.feed.FeedRepository;
import com.meokplaylist.domain.repository.place.RoadMapPlaceRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.infra.feed.Feed;
import com.meokplaylist.infra.feed.FeedPhotos;
import com.meokplaylist.infra.place.Places;
import com.meokplaylist.infra.place.RoadMapPlace;
import com.meokplaylist.security.KakaoLocalClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final KakaoLocalClient kakao;

    private static final int PAGE_SIZE = 15; // 한 번에 요청할 size (1~15)

    //FD6:음식점, CE7:카페
    private static final String RESTAURANT ="FD6";
    private static final String CAFE ="CE7";

    private final FeedRepository feedRepository;
    private final FeedPhotosRepository feedPhotosRepository;
    private final PlacesRepository placesRepository;
    private final RoadMapPlaceRepository roadMapPlaceRepository;


    @Transactional
    public  Map<Integer, List<KakaoSearchResponse.Document>> pullOutKakaoPlace(Long feedId){

        Feed feed=feedRepository.findByFeedId(feedId)
                .orElseThrow(()-> new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));

        List<FeedPhotos> feedPhotos=feedPhotosRepository.findAllByFeedFeedIdOrderBySequenceAsc(feed.getFeedId());

        Map<Integer, List<KakaoSearchResponse.Document>> KakoPlaceInfor = Map.of();

        for(FeedPhotos fp : feedPhotos){
            Double latitude=fp.getLatitude();
            Double longitude=fp.getLongitude();

            List<KakaoSearchResponse.Document> roadMapPlaceList = findAllPlaceByCategory(RESTAURANT,latitude,longitude);
            KakoPlaceInfor.put(fp.getSequence(),roadMapPlaceList);
        }

        return KakoPlaceInfor;

    }

    @Transactional
    public void saveRoadMapPlace(SaveRoadMapPlaceRequest request){

        Feed feed=feedRepository.findByFeedId(request.getFeedId())
                .orElseThrow(()-> new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));
        List<FeedPhotos> feedPhotos=feedPhotosRepository.findAllByFeedFeedIdOrderBySequenceAsc(feed.getFeedId());

        Map<Integer, KakaoSearchResponse.Document> payload = request.getSaveRoadMapPlaceInfor();

        if (payload == null || payload.isEmpty()) {
            throw new BizExceptionHandler(ErrorCode.INVALID_PLACE_PAYLOAD);
        }

        // 2-1) 사이즈 일치
        if (payload.size() != feedPhotos.size()) {
            throw new BizExceptionHandler(ErrorCode.SEQUENCE_COUNT_MISMATCH);
        }

        // 2-2) 시퀀스 집합 일치(누락/초과 탐지)
        var requestedSeqs = new java.util.HashSet<>(payload.keySet());
        var photoSeqs = feedPhotos.stream()
                .map(FeedPhotos::getSequence)
                .collect(java.util.stream.Collectors.toSet());

        if (!requestedSeqs.equals(photoSeqs)) {
            throw new BizExceptionHandler(ErrorCode.SEQUENCE_SET_MISMATCH);
        }

        List<Places> placesList=new ArrayList<>(feedPhotos.size());
        List<RoadMapPlace> roadMapPlaceList=new ArrayList<>(feedPhotos.size());

        for(FeedPhotos feedPhoto:feedPhotos){
            int seq=feedPhoto.getSequence();

            Long placeId = Long.parseLong(payload.get(seq).id());

            Places place=new Places(
                    placeId,
                    payload.get(seq).placeName(),
                    payload.get(seq).addressName(),
                    payload.get(seq).roadAddressName(),
                    payload.get(seq).placeUrl(),
                    payload.get(seq).phone(),
                    payload.get(seq).categoryGroupCode(),
                    payload.get(seq).categoryGroupName()
            );

            RoadMapPlace roadMapPlace =new RoadMapPlace(place,feedPhoto);

            placesList.add(place);
            roadMapPlaceList.add(roadMapPlace);

        }

        placesRepository.saveAll(placesList);
        roadMapPlaceRepository.saveAll(roadMapPlaceList);

    }


    @Transactional(readOnly = true)
    public List<CallInRoadMapDto> callInRoadMap(Long feedId){

        Feed feed=feedRepository.findByFeedId(feedId)
                .orElseThrow(()-> new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));

        List<RoadMapPlace> roadMapPlaceList=roadMapPlaceRepository.findAllByFeedPhotosFeedFeedIdOrderByFeedPhotos_SequenceAsc(feed.getFeedId());

        if(roadMapPlaceList ==null || roadMapPlaceList.isEmpty()){
            throw new BizExceptionHandler(ErrorCode.NOT_FOUND_ROADMAPPLACE);
        }

        List<CallInRoadMapDto> responseList =new ArrayList<>(roadMapPlaceList.size());

        for(RoadMapPlace roadMapPlace : roadMapPlaceList){
            Places place=roadMapPlace.getPlace();
            CallInRoadMapDto response = CallInRoadMapDto.builder()
                    .name(place.getName())
                    .addressName(place.getAddressName())
                    .roadAddressName(place.getRoadAddressName())
                    .phone(place.getPhone())
                    .kakaoCategoryName(place.getKakaoCategoryName())
                    .build();

            responseList.add(response);
        }

        return responseList;
    }




    public List<KakaoSearchResponse.Document> findAllPlaceByCategory(String category, double x, double y) {
        List<KakaoSearchResponse.Document> all = new ArrayList<>();
        Set<String> seenIds = new HashSet<>(); // 중복 방지용

        for (int page = 1; page <= 45; page++) {
            KakaoSearchResponse res = kakao.searchByCategory(category, x, y, page, PAGE_SIZE);
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

        return all;
    }

}
