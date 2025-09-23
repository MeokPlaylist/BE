package com.meokplaylist.domain.repository.socialInteraction;

import com.meokplaylist.infra.socialInteraction.Follows;
import com.meokplaylist.infra.user.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowsRepository extends JpaRepository<Follows, Long> {

    // 내가 팔로우하는 사용자들 (팔로잉)
    @Query("""
           select f.follower
             from Follows f
             join f.follower u
            where f.following.userId = :userId
            order by f.createdAt desc
           """)
    Slice<Users> findFollowingsUsers(@Param("userId") Long userId, Pageable pageable);

    // 나를 팔로우하는 사용자들 (팔로워)
    @Query("""
           select f.following
             from Follows f
             join f.following u
            where f.follower.userId = :userId
            order by f.createdAt desc
           """)
    Slice<Users> findFollowersUsers(@Param("userId") Long userId, Pageable pageable);

    @Query("""
           select f.follower
             from Follows f
             join f.follower u
            where f.following.nickname = :nickname
            order by f.createdAt desc
           """)
    Slice<Users> findFollowingsOtherUser(@Param("nickname") String nickname, Pageable pageable);


    @Query("""
           select f.following
             from Follows f
             join f.following u
            where f.follower.nickname = :nickname
            order by f.createdAt desc
           """)
    Slice<Users> findFollowersOtherUser(@Param("nickname") String nickname, Pageable pageable);


    boolean existsByFollowerUserIdAndFollowingUserId(Long followerId, Long followingId);

    @Query("select f.follower.userId from Follows f " +
            "where f.following.userId = :meId and f.follower.userId in :targetIds")
    List<Long> findAllFollowerIdsByFollowingId(@Param("meId") Long meId, @Param("targetIds") List<Long> targetIds);

    @Modifying
    void deleteByFollowerUserIdAndFollowingUserId(Long followerId, Long followingId);


    long countByFollowingUserId(Long userId); // 팔로워 수
    long countByFollowerUserId(Long userId);  // 팔로잉 수

}
