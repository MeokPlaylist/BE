package com.meokplaylist.domain.repository.category;

import com.meokplaylist.infra.Category.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Long> {
}
