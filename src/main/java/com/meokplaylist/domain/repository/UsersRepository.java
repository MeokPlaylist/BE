package com.meokplaylist.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.meokplaylist.infra.Users;

import java.time.LocalDate;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users,Long>{

    Optional<Users> findByEmail(String email);
    Optional<Users> findByNameAndEmailAndBirthDay(String name, String email, LocalDate birthday);
    //Optional<Users> findByEmailAndProviderUid(String email, String providerUid);
}
