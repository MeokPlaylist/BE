package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.RecommendRestaurantRequest;
import com.meokplaylist.api.dto.UrlMappedByFeedIdDto;
import com.meokplaylist.api.dto.UserPageDto;
import com.meokplaylist.api.dto.feed.FeedRegionMappingDto;
import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.domain.repository.category.LocalCategoryRepository;
import com.meokplaylist.domain.repository.feed.FeedPhotosRepository;
import com.meokplaylist.domain.repository.feed.FeedRepository;
import com.meokplaylist.domain.repository.socialInteraction.FollowsRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.infra.socialInteraction.Follows;
import com.meokplaylist.infra.user.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialInteractionService {

        private static final int CONTENT_TYPE_RESTAURANT = 39;

        private final FollowsRepository followsRepository;
        private final UsersRepository usersRepository;
        private final FeedRepository feedRepository;
        private final S3Service s3Service;
        private final WebClient tourApiWebClient;
        private final LocalCategoryRepository localCategoryRepository;
        private final FeedPhotosRepository feedPhotosRepository;

    @Transactional
    public void follow(Long followingId, String followerNickname) {

        Users follower = usersRepository.findByNickname(followerNickname)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));
        Long followerId =follower.getUserId();

        if (followerId.equals(followingId)) {
            throw new BizExceptionHandler(ErrorCode.INVALID_INPUT); // 자기 자신 팔로우 금지
        }

        if (followsRepository.existsByFollowerUserIdAndFollowingUserId(followerId, followingId)) {
            return; // 이미 팔로우 상태면 무시
        }

        Users following = usersRepository.findByUserId(followingId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));


        Follows f = new Follows();
        f.setFollower(follower);
        f.setFollowing(following);
        followsRepository.save(f);
    }

    @Transactional
    public void unfollow(Long followingId, String unFollowerNickname) {

        Users follower = usersRepository.findByNickname(unFollowerNickname)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));
        Long followerId =follower.getUserId();

        followsRepository.deleteByFollowerUserIdAndFollowingUserId(followerId, followingId);
    }

    @Transactional(readOnly = true)
    public UserPageDto userPageDistinction(Long userId, String nickName){
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        Boolean isMe=false;

        if(user.getNickname().equals(nickName)){
            isMe=true;
        }

        long feedNum = feedRepository.countByUserUserId(user.getUserId());
        long followingNum =followsRepository.countByFollowingUserId(user.getUserId());
        long followerNum =followsRepository.countByFollowerUserId(user.getUserId());
        String userNickname=user.getNickname();
        String userIntro=user.getIntroduction();
        String profileUrl=s3Service.generatePutPresignedUrl(user.getProfileImgKey());

        List<Object[]>  feedIdsGroupedByYear = feedRepository.findFeedIdsGroupedByYear(user.getUserId());
        List<FeedRegionMappingDto> feedIdsGroupedByRegion = feedRepository.findFeedIdsGroupedByRegion(user.getUserId());
        List<Long> feedIds = feedRepository.findFeedIdsByUserUserId(user.getUserId());
        List<UrlMappedByFeedIdDto> urlList = feedPhotosRepository.findByFeedFeedId(feedIds);

        Map<Long, String> urlMappedByFeedId = urlList.stream()
                .collect(Collectors.toMap(
                        UrlMappedByFeedIdDto::getFeedId, // 키: feedId를 추출합니다.
                        dto -> s3Service.generateGetPresignedUrl(dto.getFeedPhotos().getStorageKey()) // 값: DTO를 URL로 변환합니다.
                ));

        Map<Integer, List<Long>> feedIdsgroupedByYear = feedIdsGroupedByYear.stream()
                .collect(Collectors.groupingBy(
                        row -> (Integer) row[0],   // 연도
                        LinkedHashMap::new,        // 순서 유지
                        Collectors.mapping(row -> (Long) row[1], Collectors.toList())
                ));
        //feed별로 맨 앞 지역 하나만
        Map<Long, String> firstRegionByFeedId = feedIdsGroupedByRegion.stream()
                .collect(Collectors.toMap(
                        FeedRegionMappingDto::feedId,
                        dto -> dto.region() != null ? dto.region() : "기타",
                        (existing, replacement) -> existing
                ));
        //지역별 feed 매핑
        Map<String, List<Long>> feedIdsgroupedByRegion = firstRegionByFeedId.entrySet().stream()
                .collect(Collectors.groupingBy(
                        Map.Entry::getValue,
                        Collectors.mapping(Map.Entry::getKey,
                                Collectors.toList())
                ));


        UserPageDto userPageDto = UserPageDto.builder()
                .feedNum(feedNum)
                .followingNum(followingNum)
                .followerNum(followerNum)
                .userNickname(userNickname)
                .userIntro(userIntro)
                .profileUrl(profileUrl)
                .feedIdsGroupedByYear(feedIdsgroupedByYear)
                .feedIdsGroupedByRegion(feedIdsgroupedByRegion)
                .urlMappedByFeedId(urlMappedByFeedId)
                .isMe(isMe)
                .build();

        return userPageDto;
    }

    @Transactional(readOnly = true)
    public Mono<List<String>> recommendRestaurant(RecommendRestaurantRequest request){

        List<String> regions=request.getRegions();
        // 1. 요청할 지역 정보(LocalCategory)들을 Flux 스트림으로 변환

        return Flux.fromIterable(regions)
                // 1. 각 지역 문자열을 파싱하고 DB에서 LocalCategory '리스트'를 조회합니다.
                .map(raw -> {
                    String[] parts = raw.split(":", 2);
                    if (parts.length != 2) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);
                    String type = parts[0].trim();
                    String name = parts[1].trim();
                    if (type.isEmpty() || name.isEmpty()) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);

                    // 결과는 List<LocalCategory> 입니다.
                    return localCategoryRepository.findAllByTypeAndLocalName(type, name);
                })

                // 2. ✨ 여기가 핵심! ✨
                // 스트림의 각 요소인 List<LocalCategory>를 받아서,
                // 그 리스트를 다시 개별 LocalCategory의 스트림으로 펼쳐줍니다.
                // 결과적으로 Flux<List<LocalCategory>>가 Flux<LocalCategory>로 변환됩니다.
                .flatMap(localCategoryList -> Flux.fromIterable(localCategoryList))
                // 위 라인은 .flatMap(Flux::fromIterable) 로 축약할 수 있습니다.

                // 3. 이제 스트림의 각 요소는 단일 LocalCategory 객체이므로 정상적으로 처리할 수 있습니다.
                .flatMap(localCategory -> {
                    int areaCode = localCategory.getAreaCode();
                    Long sigunguCode = localCategory.getSigunguCode();

                    // 각 LocalCategory에 대해 비동기 API를 호출합니다.
                    return tourApiWebClient
                            .get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/areaBasedList1")
                                    .queryParam("areaCode", areaCode)
                                    .queryParam("sigunguCode", sigunguCode)
                                    .queryParam("contentTypeId", CONTENT_TYPE_RESTAURANT)
                                    .queryParam("numOfRows", 20)
                                    .build())
                            .retrieve()
                            .bodyToMono(String.class); // Mono<String> 반환
                })
                // 4. 병렬로 처리된 모든 API 호출 결과를 최종적으로 하나의 리스트로 모읍니다.
                .collectList();
    }


}
