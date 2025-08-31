package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.UrlMappedByFeedIdDto;
import com.meokplaylist.api.dto.UserPageResponse;
import com.meokplaylist.api.dto.feed.FeedRegionMappingDto;
import com.meokplaylist.domain.repository.UsersRepository;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialInteractionService {
        private final FollowsRepository followsRepository;
        private final UsersRepository usersRepository;
        private final FeedRepository feedRepository;
        private final S3Service s3Service;
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
    public UserPageResponse userPageDistinction(Long userId, String nickName){
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


        UserPageResponse userPageResponse =UserPageResponse.builder()
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

        return userPageResponse;
    }
}
