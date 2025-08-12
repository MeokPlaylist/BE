package com.meokplaylist.domain.repository.feed;

import com.meokplaylist.infra.feed.FeedPhotos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedPhotosRepository extends JpaRepository<FeedPhotos, Long> {

}

