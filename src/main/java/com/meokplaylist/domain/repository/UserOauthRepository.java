package com.meokplaylist.domain.repository;

import com.meokplaylist.infra.UserOauth;
import com.meokplaylist.infra.UserOauthId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserOauthRepository extends JpaRepository<UserOauth, UserOauthId> {

    @Query("""
       select o
       from UserOauth o
       join fetch o.user u
       where u.email       = :email
         and o.providerUid = :providerUid
    """)
    Optional<UserOauth> findOauthWithUser(String email, String providerUid);
}
