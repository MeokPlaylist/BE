package com.meokplaylist.infra.feed;

import com.meokplaylist.infra.socialInteraction.Comments;
import com.meokplaylist.infra.socialInteraction.Likes;
import com.meokplaylist.infra.user.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
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

    @OneToMany(mappedBy="feed") private List<Likes> likes;
    @OneToMany(mappedBy="feed") private List<Comments> comments;

    public Feed(Users user, String content, List<String> hashTag) {
        this.user = user;
        this.content = content;
        this.hashTag = hashTag;
    }


}
