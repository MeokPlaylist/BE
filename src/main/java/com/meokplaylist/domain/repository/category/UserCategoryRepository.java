package com.meokplaylist.domain.repository.category;

import com.meokplaylist.infra.Category.UserCategory;
import com.meokplaylist.infra.Category.UserCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCategoryRepository extends JpaRepository<UserCategory, UserCategoryId> {

}
