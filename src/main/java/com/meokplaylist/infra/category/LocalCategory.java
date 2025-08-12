package com.meokplaylist.infra.category;

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
    private Long localCategoryId;

    @Column
    private String type;

    @Column
    private String localName;

}
