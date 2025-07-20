package infra;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter
@Table
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class UserOauth {

    @EmbeddedId
    private UserOauthId id;

    @MapsId("userId")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            foreignKey = @ForeignKey(name = "fk_user_oauth_user"))
    private Users user;                            // ← **올바른 User 엔티티로 import**


    @MapsId("providerId")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id",
            foreignKey = @ForeignKey(name = "fk_user_oauth_provider"))
    private OauthProviders provider;

    @Column(name = "provider_uid", nullable = false)
    private String providerUid;

    @Column(name = "access_token", columnDefinition = "TEXT")
    private String accessToken;

    @Column(name = "jwt_refresh_token",length = 64)
    private String JwtRefreshToken;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "token_exp")
    private OffsetDateTime tokenExp;
}

