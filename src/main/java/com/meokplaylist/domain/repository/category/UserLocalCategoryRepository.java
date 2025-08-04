package com.meokplaylist.domain.repository.category;

import com.meokplaylist.infra.Category.UserLocalCategory;
import com.meokplaylist.infra.Category.UserLocalCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLocalCategoryRepository extends JpaRepository<UserLocalCategory, UserLocalCategoryId> {
}
