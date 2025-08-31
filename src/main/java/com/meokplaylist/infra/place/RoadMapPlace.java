package com.meokplaylist.infra.place;

import com.meokplaylist.infra.feed.FeedPhotos;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
public class RoadMapPlace {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn
    private FeedPhotos feedPhotos;

    @OneToOne
    @JoinColumn
    private Places place;

    public RoadMapPlace(Places place, FeedPhotos feedPhotos) {
        this.place = place;
        this.feedPhotos = feedPhotos;
    }
}
