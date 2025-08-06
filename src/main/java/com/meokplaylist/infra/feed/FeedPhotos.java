package com.meokplaylist.infra.feed;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class FeedPhotos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @Column
    private String storageUrl;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    private LocalDateTime dayAndTime;

    private Integer sequence; // 피드 사진 순서

    public FeedPhotos(Double longitude, Feed feed, String storageUrl, Double latitude, LocalDateTime dayAndTime, Integer sequence) {
        this.longitude = longitude;
        this.feed = feed;
        this.storageUrl = storageUrl;
        this.latitude = latitude;
        this.dayAndTime = dayAndTime;
        this.sequence = sequence;
    }
}
