package com.meokplaylist.domain.repository;

import com.meokplaylist.infra.OauthProviders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OauthProvidersRepository extends JpaRepository<OauthProviders, Integer> {
    Optional<OauthProviders> findByName(String name);
}
