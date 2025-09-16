package com.meokplaylist.domain.repository.place;

import com.meokplaylist.infra.place.Places;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlacesRepository extends JpaRepository<Places, Long> {
    Places findByLatitudeAndLongitude(Long x, Long y);
}
