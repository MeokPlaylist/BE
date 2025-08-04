package com.meokplaylist.domain.repository.category;

import com.meokplaylist.infra.Category.UserFoodCategory;
import com.meokplaylist.infra.Category.UserFoodCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFoodCategoryRepository extends JpaRepository<UserFoodCategory, UserFoodCategoryId> {

}
