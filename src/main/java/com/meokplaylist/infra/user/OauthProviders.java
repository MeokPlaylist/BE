package com.meokplaylist.infra.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@Table
@AllArgsConstructor
@NoArgsConstructor
public class OauthProviders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="provider_id")
    private Integer providerId;

    @Column(nullable = false, length=20)
    private String name;

}
