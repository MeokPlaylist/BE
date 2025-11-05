package com.meokplaylist.infra.feed;

import com.meokplaylist.infra.roadmap.RoadMapPlace;
import com.meokplaylist.infra.socialInteraction.Comments;
import com.meokplaylist.infra.socialInteraction.Likes;
import com.meokplaylist.infra.user.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Feed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private String content;

    private List<String> hashTag;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "feed",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<FeedCategory> feedCategories = new ArrayList<>();

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoadMapPlace> roadmapPlaces = new ArrayList<>();

    @OneToMany(mappedBy = "feed",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<FeedLocalCategory> feedLocalCategories = new ArrayList<>();


    @OneToMany(mappedBy = "feed",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<FeedPhotos> feedPhotos = new ArrayList<>();

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comments> comments = new ArrayList<>();

    @OneToMany(mappedBy="feed") private List<Likes> likes;

    public Feed(Users user, String content, List<String> hashTag) {
        this.user = user;
        this.content = content;
        this.hashTag = hashTag;
    }


}
