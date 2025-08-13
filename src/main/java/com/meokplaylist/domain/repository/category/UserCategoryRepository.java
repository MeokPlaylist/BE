package com.meokplaylist.domain.repository.category;

import com.meokplaylist.infra.category.UserCategory;
import com.meokplaylist.infra.category.UserCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCategoryRepository extends JpaRepository<UserCategory, UserCategoryId> {
    Optional<List<UserCategory>> findByUserUserId(Long userId);
}
