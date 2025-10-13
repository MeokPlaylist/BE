package com.meokplaylist.infra.roadmap;

import com.meokplaylist.infra.feed.FeedPhotos;
import com.meokplaylist.infra.place.Places;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "roadmap_place")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RoadMapPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private RoadMap roadMap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Places place;

    // 원본 피드 사진 (선택적, 자동 생성용)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_photo_id")
    private FeedPhotos feedPhoto;

    // 순서 관련 (예: 1일차 2번째 장소)
    private Integer dayIndex;
    private Integer orderIndex;


    // === 생성자 헬퍼 ===
    public static RoadMapPlace of(RoadMap roadMap, Places place, FeedPhotos feedPhoto, int dayIndex, int orderIndex) {
        return RoadMapPlace.builder()
                .roadMap(roadMap)
                .place(place)
                .feedPhoto(feedPhoto)
                .dayIndex(dayIndex)
                .orderIndex(orderIndex)
                .build();
    }
}
