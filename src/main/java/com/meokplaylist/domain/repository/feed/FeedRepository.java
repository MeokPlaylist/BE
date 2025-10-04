package com.meokplaylist.domain.repository.feed;

import com.meokplaylist.api.dto.feed.FeedRegionMappingDto;
import com.meokplaylist.infra.feed.Feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed,Long> {

    Optional<Feed> findByFeedId(Long feedId);

    long countByUserUserId(Long userId);

    @EntityGraph(attributePaths = {"user"})
    @Query("""
        select f
        from Feed f
        where f.user.userId in (
            select fl.following.userId
            from Follows fl
            where fl.follower.userId = :userId
        )
        order by f.createdAt desc, f.feedId desc
    """)
    Slice<Feed> findFollowingFeeds(@Param("userId") Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("""
        SELECT f
        FROM Feed f
        JOIN FeedCategory fc ON fc.feed = f
        WHERE fc.category.id IN :categoryIds
        GROUP BY f
        ORDER BY COUNT(fc.id) DESC, MAX(f.createdAt) DESC
    """)
    Slice<Feed> findCategoryIds(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

    @Query("select f.feedId from Feed f where f.user.userId = :userId")
    List<Long> findFeedIdsByUserUserId(@Param("userId") Long userId);

    @Query("SELECT EXTRACT(YEAR FROM f.createdAt), f.feedId " +
            "FROM Feed f " +
            "WHERE f.user.userId = :userId " +
            "ORDER BY f.createdAt DESC")
    List<Object[]> findFeedIdsGroupedByYear(@Param("userId") Long userId);

    @Query("""
       SELECT new com.meokplaylist.api.dto.feed.FeedRegionMappingDto(
           CONCAT(CONCAT(lc.type, ':'), lc.localName), f.feedId
       )
       FROM Feed f
       LEFT JOIN FeedLocalCategory flc ON flc.feed = f
       LEFT JOIN flc.localCategory lc
       WHERE f.user.userId = :userId
       """)
    List<FeedRegionMappingDto> findFeedIdsGroupedByRegion(@Param("userId") Long userId);

    @Query("""
    SELECT f as feed,
           COUNT(DISTINCT flc.localCategory.localCategoryId) as regionMatchCount,
           COUNT(DISTINCT fc.category.categoryId) as categoryMatchCount
    FROM Feed f
    LEFT JOIN f.feedCategories fc
    LEFT JOIN f.feedLocalCategories flc
    WHERE (:regionIds IS NULL OR flc.localCategory.localCategoryId IN :regionIds)
      AND (:categoryIds IS NULL OR fc.category.categoryId IN :categoryIds)
    GROUP BY f.id
    ORDER BY regionMatchCount DESC, categoryMatchCount DESC
    """)
    List<Object[]> findFeedsByRegionAndCategoryPriority(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("regionIds") List<Long> regionIds,
            Pageable pageable
    );
}
