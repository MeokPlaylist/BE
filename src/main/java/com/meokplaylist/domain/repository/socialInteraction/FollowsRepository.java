package com.meokplaylist.domain.repository.socialInteraction;

import com.meokplaylist.infra.socialInteraction.Follows;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowsRepository extends JpaRepository<Follows, Long> {
    long countByFollowingUserId(Long userId); // 팔로워 수
    long countByFollowerUserId(Long userId);  // 팔로잉 수
}
