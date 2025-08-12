package com.meokplaylist.infra.feed;

import com.meokplaylist.infra.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
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

    public Feed(Users user, String content, List<String> hashTag) {
        this.user = user;
        this.content = content;
        this.hashTag = hashTag;
    }
// + 좋아요, 댓글

}
