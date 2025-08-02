package com.meokplaylist.domain.repository.category;

import com.meokplaylist.infra.Category.LocalCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalCategoryRepository extends JpaRepository<LocalCategory, Long> {
}
