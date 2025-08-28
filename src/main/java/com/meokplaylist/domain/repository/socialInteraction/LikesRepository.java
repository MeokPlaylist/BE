package com.meokplaylist.domain.repository.socialInteraction;


import com.meokplaylist.api.dto.LikeCountDto;
import com.meokplaylist.infra.socialInteraction.Likes;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface LikesRepository extends JpaRepository<Likes,Long> {

    List<LikeCountDto> countByFeedFeedId(List<Long> feedIds);
}
