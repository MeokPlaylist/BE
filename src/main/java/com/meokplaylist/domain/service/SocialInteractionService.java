package com.meokplaylist.domain.service;

import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.domain.repository.socialInteraction.FollowsRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.infra.socialInteraction.Follows;
import com.meokplaylist.infra.user.Users;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SocialInteractionService {
        private final FollowsRepository followsRepository;
        private final UsersRepository usersRepository;


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



}
