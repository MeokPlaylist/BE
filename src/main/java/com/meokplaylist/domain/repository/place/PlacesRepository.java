package com.meokplaylist.domain.repository.place;

import com.meokplaylist.infra.place.Places;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlacesRepository extends JpaRepository<Places, Long> {
    Places findByLatitudeAndLongitude(Double lat, Double lng);
    Optional<Places> findById(Long placeId);
}
