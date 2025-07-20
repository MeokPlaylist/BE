package com.meokplaylist.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.meokplaylist.infra.Users;

public interface UsersRepository extends JpaRepository<Users,Long>{

    String findByEmail(String email);
}
