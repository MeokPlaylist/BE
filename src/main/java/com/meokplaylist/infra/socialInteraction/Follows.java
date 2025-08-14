package com.meokplaylist.infra.socialInteraction;

import com.meokplaylist.infra.user.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Follows {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 팔로우 하는 사람 (주체)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private Users follower;

    // 팔로우 당하는 사람 (대상)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private Users following;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

}
