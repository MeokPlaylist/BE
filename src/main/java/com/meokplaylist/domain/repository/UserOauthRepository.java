package com.meokplaylist.domain.repository;

import com.meokplaylist.infra.user.UserOauth;
import com.meokplaylist.infra.user.UserOauthId;
import com.meokplaylist.infra.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOauthRepository extends JpaRepository<UserOauth, UserOauthId> {

    Optional<UserOauth> findByProviderUid(String providerUid);
    UserOauth findByUser(Users user);

}
