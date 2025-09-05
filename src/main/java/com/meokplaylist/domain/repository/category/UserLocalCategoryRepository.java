package com.meokplaylist.domain.repository.category;

import com.meokplaylist.infra.category.UserCategory;
import com.meokplaylist.infra.category.UserLocalCategory;
import com.meokplaylist.infra.category.UserLocalCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserLocalCategoryRepository extends JpaRepository<UserLocalCategory, UserLocalCategoryId> {
    List<UserLocalCategory> findByUserUserId(Long userId);
}
