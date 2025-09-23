package com.meokplaylist.infra.place;

import com.meokplaylist.infra.user.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "saved_places",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "place_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoritePlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Places place;

    @CreationTimestamp
    private OffsetDateTime createdAt ;
}
