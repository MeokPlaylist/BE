package com.meokplaylist.domain.repository.category;

import com.meokplaylist.infra.category.LocalCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalCategoryRepository extends JpaRepository<LocalCategory, Long> {
    LocalCategory findByTypeAndLocalName(String type, String localName);

    List<LocalCategory> findAllByTypeAndLocalName(String type, String localName);
}
