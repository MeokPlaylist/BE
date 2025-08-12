package com.meokplaylist.domain.repository.category;

import com.meokplaylist.infra.category.UserLocalCategory;
import com.meokplaylist.infra.category.UserLocalCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLocalCategoryRepository extends JpaRepository<UserLocalCategory, UserLocalCategoryId> {
}
