package com.meokplaylist.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.meokplaylist.infra.Users;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users,Long>{
    @Query("""
       select o
       from Users o
       WHERE o.email = :email AND o.passwordHash IS NOT NULL
    """)
    Optional<Users> findByEmail(String email);

    Optional<Users> findByUserId(Long userId);

    Optional<Users> findByUserIdAndNameAndEmailAndBirthDay(Long userId,String name, String email, LocalDate birthday);

    Optional<Users> findByEmailAndPasswordHash(String email,String password);

}
