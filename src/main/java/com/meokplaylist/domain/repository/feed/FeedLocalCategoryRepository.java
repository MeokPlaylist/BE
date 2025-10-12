package com.meokplaylist.domain.repository.feed;

import com.meokplaylist.infra.feed.FeedLocalCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedLocalCategoryRepository extends JpaRepository<FeedLocalCategory,Long> {

    List<FeedLocalCategory> findByFeedFeedId(Long feedId);
}
