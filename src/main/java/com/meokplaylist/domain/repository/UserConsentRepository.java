package com.meokplaylist.domain.repository;

import com.meokplaylist.infra.UserConsent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserConsentRepository extends JpaRepository<UserConsent, Long> {
    Optional<UserConsent> findByUserUserId(Long UserId);
}
