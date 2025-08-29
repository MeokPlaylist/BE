package com.meokplaylist.infra.socialInteraction;

import com.meokplaylist.infra.feed.Feed;
import com.meokplaylist.infra.user.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "comments",
        indexes = {
                @Index(name = "idx_comments_feed", columnList = "feed_id"),
                @Index(name = "idx_comments_user", columnList = "author_id")
        }
)
@Getter
@NoArgsConstructor
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Users author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comments parent; // 대댓글

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    public Comments(Feed feed, Users author, String content) {
        this.feed = feed;
        this.author = author;
        this.content = content;
    }
    
}