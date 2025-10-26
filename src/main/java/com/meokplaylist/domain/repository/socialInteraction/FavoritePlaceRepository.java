package com.meokplaylist.domain.repository.socialInteraction;

import com.meokplaylist.infra.place.FavoritePlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace, Long> {
    boolean existsByUserUserIdAndPlaceId(Long userId, Long placeId);
    List<FavoritePlace> findAllByUserUserId(Long userId);
    Optional<FavoritePlace> findByUserUserIdAndPlaceId(Long userId,Long placeId);
    Optional<FavoritePlace> findByUserUserIdAndPlaceLatitudeAndPlaceLongitude(Long userId, double lat, double lng);
}
