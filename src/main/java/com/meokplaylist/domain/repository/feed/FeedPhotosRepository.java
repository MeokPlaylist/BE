package com.meokplaylist.domain.repository.feed;

import com.meokplaylist.infra.feed.FeedPhotos;
import com.meokplaylist.infra.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedPhotosRepository extends JpaRepository<FeedPhotos, Long> {
    //FeedPhotos findByFeedIdAndSequence(Long feedId, Integer sequence);

    @Query("SELECT p FROM FeedPhotos p " +
            "WHERE p.feed.user = :user AND p.sequence = 0 " +
            "ORDER BY p.feed.createdAt DESC") // 최신 피드 순으로 정렬
    List<FeedPhotos> findThumbnailsByUser(@Param("user") Users user);
}

