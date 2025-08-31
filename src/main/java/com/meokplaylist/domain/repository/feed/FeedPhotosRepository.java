package com.meokplaylist.domain.repository.feed;


import com.meokplaylist.api.dto.UrlMappedByFeedIdDto;
import com.meokplaylist.infra.feed.FeedPhotos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedPhotosRepository extends JpaRepository<FeedPhotos, Long> {

//    @Query("""
//    SELECT new com.meokplaylist.api.dto.FeedPhotosWithLocalDto(lc.localName, p)
//    FROM FeedPhotos p
//    JOIN p.feed f
//    JOIN FeedLocalCategory flc ON flc.feed = f
//    JOIN flc.localCategory lc
//    WHERE f.user = :user
//        AND p.sequence = 0
//    ORDER BY lc.type ASC,
//           lc.localName ASC,
//           f.createdAt DESC,
//           f.feedId DESC
//    """)
//    Slice<FeedPhotosWithLocalDto> findThumbnailsByUserOrderInLocal(@Param("user") Users user, Pageable pageable);

    @Query("SELECT new com.meokplaylist.api.dto.UrlMappedByFeedIdDto(p.feed.feedId,p) " +
            "FROM FeedPhotos p " +
            "WHERE p.feed.feedId in :feedIds AND p.sequence = 1 " +
            "ORDER BY p.feed.createdAt DESC")
    List<UrlMappedByFeedIdDto> findByFeedFeedId(@Param("feedIds") List<Long> feedIds);

    List<FeedPhotos> findAllByFeedFeedIdInOrderByFeedFeedIdAscSequenceAsc(List<Long> feedId);

    List<FeedPhotos> findAllByFeedFeedIdOrderBySequenceAsc(Long feedId);
}

