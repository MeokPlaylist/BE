package com.meokplaylist.domain.repository.roadmap;

import com.meokplaylist.infra.feed.Feed;
import com.meokplaylist.infra.roadmap.RoadMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoadMapRepository extends JpaRepository<RoadMap, Long> {


    Optional<RoadMap> findByFeed(Feed feed);
}
