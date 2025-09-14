package com.meokplaylist.infra.socialInteraction;

import com.meokplaylist.infra.user.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "follows",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_follower_following",
                columnNames = {"follower_id", "following_id"}
        ),
        indexes = {
                @Index(name = "idx_follows_follower_id", columnList = "follower_id"),
                @Index(name = "idx_follows_following_id", columnList = "following_id")
        }
)
public class Follows {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 팔로우 하는 사람 (주체)
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private Users follower;

    // 팔로우 당하는 사람 (대상)
    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private Users following;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

}
