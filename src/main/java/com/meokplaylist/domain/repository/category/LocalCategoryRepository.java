package com.meokplaylist.domain.repository.category;

import com.meokplaylist.infra.Category.LocalCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalCategoryRepository extends JpaRepository<LocalCategory, Long> {
    List<LocalCategory> findAllByLocalNameIn(List<String> names);
}
