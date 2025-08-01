package com.meokplaylist.infra.Category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class LocalCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="local_category_id")
    private Long localCategoryId;

    @Column
    private String localBigObject;

    @Column
    private String localSmallObject;

    public LocalCategory(String localSmallObject, String localBigObject) {
        this.localSmallObject = localSmallObject;
        this.localBigObject = localBigObject;
    }
}
