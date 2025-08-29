package com.meokplaylist.infra.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column
    private String email;

    @Column
    private String name;
    @Column
    private LocalDate birthDay;

    @Column(columnDefinition = "TEXT")
    private String passwordHash;

    @Column(name = "nickname", unique = true , length = 50)
    private String nickname;

    @Column
    private String profileImgKey;

    @Column(name = "introduction")
    private String introduction;

    @Column
    @Builder.Default
    private Boolean checkstatus=false;

    @Column(name = "jwt_access_token", length = 2048)
    private String jwtAccessToken;

    @Column(name = "jwt_refresh_token", length = 2048)
    private String jwtRefreshToken;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Builder
    public Users(String email, String passwordHash,String name,LocalDate birthDay){
        this.email=email;
        this.passwordHash=passwordHash;
        this.name=name;
        this.birthDay=birthDay;
    }
}