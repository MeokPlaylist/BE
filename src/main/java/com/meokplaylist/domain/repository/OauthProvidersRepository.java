package com.meokplaylist.domain.repository;

import com.meokplaylist.infra.user.OauthProviders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OauthProvidersRepository extends JpaRepository<OauthProviders, Integer> {
    OauthProviders findByName(String name);
}
