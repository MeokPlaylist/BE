package com.meokplaylist.domain.repository.category;

import com.meokplaylist.infra.Category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodCategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByNameIn(List<String> names);
}
