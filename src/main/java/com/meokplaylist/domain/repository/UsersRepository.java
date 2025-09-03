package com.meokplaylist.domain.repository;


import com.meokplaylist.api.dto.SearchUserDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import com.meokplaylist.infra.user.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users,Long>{

    Optional<Users> findByEmailAndPasswordHashIsNotNull(String email);

    Optional<Users> findByUserId(Long userId);

    Optional<Users> findByEmail(String Email);

    Optional<Users> findByNickname(String nickname);

    @Query("SELECT new com.meokplaylist.api.dto.SearchUserDto(u.nickname, u.introduction) " +
            "FROM Users u " +
            "WHERE u.nickname LIKE CONCAT(:prefix, '%')")
    Slice<SearchUserDto> findUsersByNicknamePrefix(@Param("prefix") String prefix, Pageable pageable);


}
