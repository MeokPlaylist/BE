package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.FeedPhotosWithYearDto;
import com.meokplaylist.api.dto.UserPageResponse;
import com.meokplaylist.api.dto.user.MypageResponse;
import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.domain.repository.feed.FeedPhotosRepository;
import com.meokplaylist.domain.repository.feed.FeedRepository;
import com.meokplaylist.domain.repository.socialInteraction.FollowsRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.infra.socialInteraction.Follows;
import com.meokplaylist.infra.user.Users;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class SocialInteractionService {
        private final FollowsRepository followsRepository;
        private final UsersRepository usersRepository;
        private final FeedRepository feedRepository;
        private final S3Service s3Service;
        private final FeedPhotosRepository feedPhotosRepository;

    @Transactional
    public void follow(Long followerId, String followingNickname) {

        Users following = usersRepository.findByNickname(followingNickname)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));
        Long followingId =following.getUserId();

        if (followerId.equals(followingId)) {
            throw new BizExceptionHandler(ErrorCode.INVALID_INPUT); // 자기 자신 팔로우 금지
        }

        if (followsRepository.existsByFollowerUserIdAndFollowingUserId(followerId, followingId)) {
            return; // 이미 팔로우 상태면 무시
        }

        Users follower = usersRepository.findByUserId(followerId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));


        Follows f = new Follows();
        f.setFollower(follower);
        f.setFollowing(following);
        followsRepository.save(f);
    }

    @Transactional
    public void unfollow(Long followerId, String unFollowingNickname) {

        Users following = usersRepository.findByNickname(unFollowingNickname)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));
        Long followingId =following.getUserId();

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
        List<FeedPhotosWithYearDto> thumbnailPhotos = feedPhotosRepository.findThumbnailsByUser(user);

        Map<Integer, List<String>> groupedUrlsByYear = thumbnailPhotos.stream()
                .collect(Collectors.groupingBy(
                        FeedPhotosWithYearDto::getYear,
                        // 3. 각 그룹의 FeedPhotos 객체를 S3 URL로 변환하여 리스트로 만듭니다.
                        Collectors.mapping(
                                dto -> s3Service.generateGetPresignedUrl(dto.getFeedPhoto().getStorageKey()),
                                Collectors.toList()
                        )
                ));


        UserPageResponse userPageResponse =UserPageResponse.builder()
                .feedNum(feedNum)
                .followingNum(followingNum)
                .followerNum(followerNum)
                .userNickname(userNickname)
                .userIntro(userIntro)
                .profileUrl(profileUrl)
                .feedMainPhotoUrls(groupedUrlsByYear)
                .isMe(isMe)
                .build();

        return userPageResponse;
    }


}
