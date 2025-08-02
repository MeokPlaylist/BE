package com.meokplaylist.domain.repository;

import com.meokplaylist.infra.UserConsent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConsentRepository extends JpaRepository<UserConsent, Long> {

}
