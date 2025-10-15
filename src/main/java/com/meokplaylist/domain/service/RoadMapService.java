package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.KakaoSearchResponse;
import com.meokplaylist.api.dto.place.SaveRoadMapPlaceRequest;
import com.meokplaylist.api.dto.roadmap.RoadMapCandidateDto;
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
        roadMapRepository.save(roadMap);

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
            if (photo.getLatitude() == null || photo.getLongitude() == null) continue;
            double lat = photo.getLatitude();
            double lng = photo.getLongitude();
            System.out.println("lat:"+lat+" lng:"+lng);
            //현재 사진의 날짜 구하기
            LocalDateTime dateTime;
            if (photo.getDayAndTime() instanceof LocalDateTime) {
                dateTime = (LocalDateTime) photo.getDayAndTime();
            } else {
                dateTime = LocalDateTime.parse(photo.getDayAndTime().toString(), formatter);
            }
            LocalDate currentDate = dateTime.toLocalDate();

            // 첫날 설정 (정렬된 리스트의 첫 번째 날짜)
            if (firstDay == null) firstDay = currentDate;

            // 일차 계산
            int dayIndex = (int) ChronoUnit.DAYS.between(firstDay, currentDate) + 1;

            // Kakao에서 장소 후보 전부 가져오기
            List<String> categories = List.of("FD6", "CE7");
            List<KakaoSearchResponse.Document> docs = new ArrayList<>();
            for (String category : categories) {
                docs.addAll(placeService.findAllPlaceByCategory(lat, lng));
            }
            if (docs.isEmpty()) continue;

            // Places 캐싱
            List<Places> candidatePlaces = new ArrayList<>();
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
        return candidateDtos;
    }

    @org.springframework.transaction.annotation.Transactional
    public void saveRoadMapPlace(SaveRoadMapPlaceRequest request) {

        // 1) feedId로 로드맵 찾기
        Feed feed = feedRepository.findByFeedId(request.getFeedId())
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));

        RoadMap roadMap = roadMapRepository.findByFeed(feed)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.NOT_FOUND_ROADMAP));

        // 2) 타이틀 갱신
        roadMap.setTitle(request.getTitle());
        roadMapRepository.save(roadMap);

        // 3) 요청된 매핑 정보 검증
        Map<Long, Long> payload = request.getSaveRoadMapPlaceInfor(); // roadMapPlaceId → placeId

        if (payload == null || payload.isEmpty()) {
            throw new BizExceptionHandler(ErrorCode.INVALID_PLACE_PAYLOAD);
        }

        // 4) 각 로드맵플레이스 수정
        List<RoadMapPlace> roadMapPlaces = roadMapPlaceRepository.findAllByRoadMap(roadMap);

        for (RoadMapPlace rmp : roadMapPlaces) {
            Long newPlaceId = payload.get(rmp.getId());
            if (newPlaceId == null) continue; // 매핑 없는 경우 스킵

            Places newPlace = placesRepository.findById(newPlaceId)
                    .orElseThrow(() -> new BizExceptionHandler(ErrorCode.NOT_FOUND_PLACE));

            rmp.setPlace(newPlace);
        }

        roadMapPlaceRepository.saveAll(roadMapPlaces);
    }

}
