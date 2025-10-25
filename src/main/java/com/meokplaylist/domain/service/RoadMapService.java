package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.KakaoSearchResponse;
import com.meokplaylist.api.dto.roadmap.*;
import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.domain.repository.feed.FeedPhotosRepository;
import com.meokplaylist.domain.repository.feed.FeedRepository;
import com.meokplaylist.domain.repository.place.PlacesRepository;
import com.meokplaylist.domain.repository.roadmap.RoadMapPlaceRepository;
import com.meokplaylist.domain.repository.roadmap.RoadMapRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.infra.feed.Feed;
import com.meokplaylist.infra.feed.FeedPhotos;
import com.meokplaylist.infra.place.Places;
import com.meokplaylist.infra.roadmap.RoadMap;
import com.meokplaylist.infra.roadmap.RoadMapPlace;
import com.meokplaylist.infra.user.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class RoadMapService {

    private final FeedRepository feedRepository;
    private final FeedPhotosRepository feedPhotosRepository;
    private final PlacesRepository placesRepository;
    private final RoadMapRepository roadMapRepository;
    private final RoadMapPlaceRepository roadMapPlaceRepository;
    private final UsersRepository usersRepository;
    private final PlaceService placeService; // category 검색용
    private final S3Service s3Service;

    /**
     * Feed ID로 로드맵 전체 생성 및 반환
     * - FeedPhotos의 위도/경도 기반으로 Kakao API에서 장소 목록 조회
     * - Places에 캐싱 후 RoadMapPlace 생성
     * - 최종 DTO 리스트 반환
     */
    @Transactional
    public List<RoadMapCandidateDto> createRoadMap(Long feedId, Long userId) {

        Feed feed = feedRepository.findByFeedId(feedId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        RoadMap roadMap = RoadMap.builder()
                .feed(feed)
                .user(user)
                .title(null)
                .build();

        List<RoadMapCandidateDto> candidateDtos = new ArrayList<>();
        // FeedPhotos를 dayAndTime 기준으로 정렬
        List<FeedPhotos> photos = feedPhotosRepository.findByFeed_FeedId(feedId).stream()
                .sorted(Comparator.comparing(photo -> {
                    // dayAndTime이 String이면 LocalDateTime으로 변환
                    if (photo.getDayAndTime() instanceof LocalDateTime dt) return dt;
                    return LocalDateTime.parse(photo.getDayAndTime().toString(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                }))
                .toList();

        // 기준 날짜 계산
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDate firstDay = null;

        int orderIndex = 1;
        for (FeedPhotos photo : photos) {
            double lat;
            double lng;
            if (photo.getLatitude() == null || photo.getLongitude() == null) {
                lat=0;
                lng=0;
            }else{
                lat = photo.getLatitude();
                lng = photo.getLongitude();
            }
            System.out.println("lat:"+lat+" lng:"+lng);
            //현재 사진의 날짜 구하기
            LocalDateTime dateTime;
            if (photo.getDayAndTime() instanceof LocalDateTime) {
                dateTime = (LocalDateTime) photo.getDayAndTime();
            } else {
                dateTime = LocalDateTime.parse(photo.getDayAndTime().toString(), formatter);
            }
            roadMap.setFirstPlaceDayAndTime(dateTime);
            roadMapRepository.save(roadMap);

            LocalDate currentDate = dateTime.toLocalDate();

            // 첫날 설정 (정렬된 리스트의 첫 번째 날짜)
            if (firstDay == null) firstDay = currentDate;

            // 일차 계산
            int dayIndex = (int) ChronoUnit.DAYS.between(firstDay, currentDate) + 1;
            
            // Kakao에서 장소 후보 전부 가져오기
            List<Places> candidatePlaces = new ArrayList<>();
            if(lat!=0&lng!=0) {
                List<KakaoSearchResponse.Document> docs = placeService.findAllPlaceByCategory(lat, lng);

                // Places 캐싱
                for (KakaoSearchResponse.Document doc : docs) {
                    Long kakaoId = Long.parseLong(doc.id());
                    Places place = placesRepository.findById(kakaoId)
                            .orElseGet(() -> placesRepository.save(new Places(
                                    kakaoId,
                                    doc.placeName(),
                                    doc.addressName(),
                                    doc.roadAddressName(),
                                    doc.placeUrl(),
                                    lat,
                                    lng,
                                    doc.phone(),
                                    doc.categoryGroupCode(),
                                    doc.categoryGroupName()
                            )));
                    candidatePlaces.add(place);
                }
            }
            // 순서대로 RoadMapPlace 생성
            RoadMapPlace rmp = RoadMapPlace.of(roadMap, null, photo, dayIndex, orderIndex++);
            roadMapPlaceRepository.save(rmp);

            String photoUrl = s3Service.generateGetPresignedUrl(photo.getStorageKey());

            candidateDtos.add(new RoadMapCandidateDto(
                    rmp.getId(),
                    photoUrl,
                    photo.getDayAndTime().toString(),
                    dayIndex,
                    orderIndex - 1,
                    candidatePlaces.stream()
                            .map(RoadMapCandidateDto.PlaceCandidate::from)
                            .toList()
            ));
        }
        System.out.println(candidateDtos);
        return candidateDtos;
    }

    @Transactional
    public void saveRoadMapPlace(SaveRoadMapPlaceRequest request) {

        // 1) feedId로 로드맵 찾기
        Feed feed = feedRepository.findByFeedId(request.getFeedId())
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));

        RoadMap roadMap = roadMapRepository.findByFeed(feed)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.NOT_FOUND_ROADMAP));

        // 2) 타이틀 갱신
        roadMap.setTitle(request.getTitle());
        roadMapRepository.save(roadMap);

        // 3) 요청 검증
        List<SaveRoadMapPlaceItem> places = request.getPlaces();
        if (places == null || places.isEmpty()) {
            throw new BizExceptionHandler(ErrorCode.INVALID_PLACE_PAYLOAD);
        }

        // 4) 로드맵에 포함된 기존 place 목록 조회
        List<RoadMapPlace> roadMapPlaces = roadMapPlaceRepository.findAllByRoadMap(roadMap);

        // 5) 요청 데이터 순회 처리
        for (SaveRoadMapPlaceItem dto : places) {
            RoadMapPlace rmp = roadMapPlaces.stream()
                    .filter(p -> p.getId().equals(dto.getRoadMapPlaceId()))
                    .findFirst()
                    .orElse(null);

            if (rmp == null) continue;

            // 🔹 후보 선택한 경우
            if (dto.getSelectedPlaceId() != null) {
                Places newPlace = placesRepository.findById(dto.getSelectedPlaceId())
                        .orElseThrow(() -> new BizExceptionHandler(ErrorCode.NOT_FOUND_PLACE));
                rmp.setPlace(newPlace);
                rmp.setCustomPlaceName(null);
                rmp.setCustomAddress(null);
            }
            // 🔹 직접 입력한 경우
            else if (dto.getCustomPlaceName() != null && !dto.getCustomPlaceName().isBlank()) {
                rmp.setPlace(null);
                rmp.setCustomPlaceName(dto.getCustomPlaceName());
                rmp.setCustomAddress(dto.getCustomAddress());
            }
            // 🔹 둘 다 없는 경우 스킵
            else {
                continue;
            }
        }

        // 6) 일괄 저장
        roadMapPlaceRepository.saveAll(roadMapPlaces);
    }


    @Transactional
    public LoadRoadMapInfor LoadRoadMapPlace(Long feedId, Long userId){

        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        Feed feed =feedRepository.findByFeedId(feedId)
                .orElseThrow(()-> new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));

        RoadMap roadMap = roadMapRepository.findByFeed(feed)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.NOT_FOUND_ROADMAP));

        String roadMaptitle=roadMap.getTitle();
        String firstDayAndTime =roadMap.getFirstPlaceDayAndTime().toString();
        Boolean isMine=false;
        if(userId.equals(feed.getUser().getUserId())){
            isMine=true;
        }
        List<LoadRoadMapPlaces> loadRoadMapPlaces=roadMap.getPlaces().stream()
                .map(p-> {
                    Places place=p.getPlace();
                    String name;
                    String address;
                    String phone = null;
                    if(place==null){
                        name=p.getCustomPlaceName();
                        address=p.getCustomAddress();
                    }
                    else {
                        name=place.getName();
                        address=place.getAddressName();
                        phone= place.getPhone();
                    }
                    return new LoadRoadMapPlaces(
                            p.getRoadMap().getId(),
                            name,
                            address,
                            phone,
                            s3Service.generateGetPresignedUrl(p.getFeedPhoto().getStorageKey()),
                            p.getDayIndex(),
                            p.getOrderIndex()
                            );
                }).toList();

        return new LoadRoadMapInfor(roadMaptitle,isMine,firstDayAndTime,loadRoadMapPlaces);
    }
}
