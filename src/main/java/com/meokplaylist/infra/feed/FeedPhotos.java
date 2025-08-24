package com.meokplaylist.infra.feed;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FeedPhotos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @Column
    private String storageKey;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    private LocalDateTime dayAndTime;

    private Integer sequence; // 피드 사진 순서

    public FeedPhotos(Double longitude, Feed feed, String storageKey, Double latitude, LocalDateTime dayAndTime, Integer sequence) {
        this.longitude = longitude;
        this.feed = feed;
        this.storageKey = storageKey;
        this.latitude = latitude;
        this.dayAndTime = dayAndTime;
        this.sequence = sequence;
    }
}
