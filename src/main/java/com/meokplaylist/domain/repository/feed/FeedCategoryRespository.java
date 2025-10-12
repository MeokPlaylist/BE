package com.meokplaylist.domain.repository.feed;

import com.meokplaylist.infra.feed.FeedCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedCategoryRespository extends JpaRepository<FeedCategory, Long> {

    List<FeedCategory> findByFeedFeedId(Long feedId);
}
