package com.meokplaylist.domain.repository.place;

import com.meokplaylist.infra.place.RoadMapPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadMapPlaceRepository extends JpaRepository<RoadMapPlace,Long> {

    List<RoadMapPlace> findAllByFeedFeedIdInOrderByFeedPhotoSequenceAsc(Long feedId);
}
