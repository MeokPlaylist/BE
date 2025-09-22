package com.meokplaylist.infra.user;

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
    private Users user;


    @MapsId("providerId")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id",
            foreignKey = @ForeignKey(name = "fk_user_oauth_provider"))
    private OauthProviders provider;

    @Column(name = "provider_uid", nullable = false)
    private String providerUid;

    @Column(name = "social_access_token", columnDefinition = "TEXT")
    private String socialAccessToken;

    @Column(name = "social_refresh_token", columnDefinition = "TEXT")
    private String socialRefreshToken;

    @Column(name = "token_exp")
    private OffsetDateTime tokenExp;

    public UserOauth(Users user, OauthProviders provider, String providerUid) {
        this.user = user;
        this.provider = provider;
        this.providerUid = providerUid;
        this.id = new UserOauthId(user.getUserId(), provider.getProviderId());
    }
}

