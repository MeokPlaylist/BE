package com.meokplaylist.domain.repository.category;

import com.meokplaylist.infra.category.UserCategory;
import com.meokplaylist.infra.category.UserCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCategoryRepository extends JpaRepository<UserCategory, UserCategoryId> {

    List<UserCategory> findByUserUserId(Long userId);

  @Query("SELECT uc.category.categoryId FROM UserCategory uc WHERE uc.user.userId = :userId")
  List<Long> findCategoryIdsByUserId(@Param("userId") Long userId);
}
