package com.meokplaylist.domain.repository.category;

import com.meokplaylist.infra.Category.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Long> {
    List<FoodCategory> findAllByNameIn(List<String> names);
}
