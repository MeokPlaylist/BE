package com.meokplaylist.domain.repository.roadmap;

import com.meokplaylist.infra.roadmap.RoadMap;
import com.meokplaylist.infra.roadmap.RoadMapPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadMapPlaceRepository extends JpaRepository<RoadMapPlace,Long> {

    List<RoadMapPlace> findAllByFeedPhotosFeedFeedIdOrderByFeedPhotos_SequenceAsc(Long feedId);

    List<RoadMapPlace> findAllByRoadMap(RoadMap roadMap);
}
