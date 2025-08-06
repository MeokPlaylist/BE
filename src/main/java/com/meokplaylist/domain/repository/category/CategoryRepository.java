package com.meokplaylist.domain.repository.category;

import com.meokplaylist.infra.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByNameIn(List<String> names);
}
