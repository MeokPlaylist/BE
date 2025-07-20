package com.meokplaylist.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.meokplaylist.infra.Users;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users,Long>{

    Optional<String> findByEmail(String email);
    Optional<Users> findByNameAndEmailAndBirthDay(String name, String email, String birthday);
}
