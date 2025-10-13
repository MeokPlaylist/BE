package com.meokplaylist.domain.repository.feed;


import com.meokplaylist.api.dto.UrlMappedByFeedIdDto;
import com.meokplaylist.infra.feed.Feed;
import com.meokplaylist.infra.feed.FeedPhotos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeedPhotosRepository extends JpaRepository<FeedPhotos, Long> {

    @Query("SELECT new com.meokplaylist.api.dto.UrlMappedByFeedIdDto(p.feed.feedId,p) " +
            "FROM FeedPhotos p " +
            "WHERE p.feed.feedId in :feedIds AND p.sequence = 1 " +
            "ORDER BY p.feed.createdAt DESC")
    List<UrlMappedByFeedIdDto> findByFeedFeedId(@Param("feedIds") List<Long> feedIds);

    List<FeedPhotos> findByFeedId(Long feedId);

    List<FeedPhotos> findAllByFeedFeedIdInOrderByFeedFeedIdAscSequenceAsc(List<Long> feedId);

    List<FeedPhotos> findAllByFeedFeedIdOrderBySequenceAsc(Long feedId);

    Optional<FeedPhotos> findByFeedFeedIdAndSequence(Long feedId, int sequence);
}

