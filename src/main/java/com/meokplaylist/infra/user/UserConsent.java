package com.meokplaylist.infra.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Table
@AllArgsConstructor
public class UserConsent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long consentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            foreignKey = @ForeignKey(name = "fk_user_consent"))
    private Users user;

    @Column
    private String version;

    @Column
    private String consentUrl;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime agreedAt;

    public UserConsent(Users user, String version, String consentUrl){
        this.user=user;
        this.version=version;
        this.consentUrl=consentUrl;
    }



}
