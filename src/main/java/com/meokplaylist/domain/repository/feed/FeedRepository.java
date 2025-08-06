package com.meokplaylist.domain.repository.feed;

import com.meokplaylist.infra.feed.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed,Long> {

    Optional<Feed> findByFeedId(Long feedId);
}
