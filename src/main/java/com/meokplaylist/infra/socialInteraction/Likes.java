package com.meokplaylist.infra.socialInteraction;

import com.meokplaylist.infra.feed.Feed;
import com.meokplaylist.infra.user.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_likes_user_feed", columnNames = {"user_id", "feed_id"})
        },
        indexes = {
                @Index(name = "idx_likes_feed", columnList = "feed_id"),
                @Index(name = "idx_likes_user", columnList = "user_id")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id",
            foreignKey = @ForeignKey(name = "fk_likes_user"))
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "feed_id",
            foreignKey = @ForeignKey(name = "fk_likes_feed"))
    private Feed feed;


    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime  createdAt;

    public Likes(Users user, Feed feed) {
        this.user = user;
        this.feed = feed;
    }
}
