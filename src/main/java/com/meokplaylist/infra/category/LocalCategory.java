package com.meokplaylist.infra.category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocalCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long localCategoryId;

    @Column
    private String type;

    @Column
    private String localName;

    @Column(name="area_code")
    private Integer areaCode;


    @Column(name="sigungu_code")
    private Long sigunguCode;

}
