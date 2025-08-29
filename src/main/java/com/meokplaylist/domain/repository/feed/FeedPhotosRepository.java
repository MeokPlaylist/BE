package com.meokplaylist.domain.repository.feed;


import com.meokplaylist.infra.feed.FeedPhotos;
import com.meokplaylist.infra.user.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedPhotosRepository extends JpaRepository<FeedPhotos, Long> {
    //FeedPhotos findByFeedIdAndSequence(Long feedId, Integer sequence);

    @Query("SELECT p FROM FeedPhotos p " +
            "WHERE p.feed.user = :user AND p.sequence = 0 " +
            "ORDER BY p.feed.createdAt DESC, p.sequence asc") // 최신 피드 순으로 정렬
    List<FeedPhotos> findThumbnailsByUser(@Param("user") Users user);

    @Query("""
    SELECT DISTINCT p
    FROM FeedPhotos p
    JOIN p.feed f,
           FeedLocalCategory flc
    JOIN flc.localCategory lc
    WHERE f.user = :user
        AND p.sequence = 0
        AND flc.feed = f
    ORDER BY lc.type ASC,
           lc.localName ASC,
           f.createdAt DESC,
           f.feedId DESC
    """)
    Slice<FeedPhotos> findThumbnailsByUserOrderInLocal(@Param("user") Users user, Pageable pageable);


    List<FeedPhotos> findAllByFeedFeedIdInOrderByFeedFeedIdAscSequenceAsc(List<Long> feedId);
}

