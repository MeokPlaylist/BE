package com.meokplaylist.infra.Category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
public class FoodCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="food_category_id")
    private Long foodCategortId;


    @Column(nullable = false)
    private String moodBigObject;

    @Column(nullable = false)
    private String moodSmallObject;

    @Column(nullable = false)
    private String foodBigObject;


    @Column(nullable = false)
    private String foodSmallObject;

    @Column(nullable = false)
    private String companionBigObject;

    @Column(nullable = false)
    private String companionSmallObject;

    public FoodCategory(String companionSmallObject, String companionBigObject, String foodBigObject, String foodSmallObject, String moodSmallObject, String moodBigObject) {
        this.companionSmallObject = companionSmallObject;
        this.companionBigObject = companionBigObject;
        this.foodBigObject = foodBigObject;
        this.foodSmallObject = foodSmallObject;
        this.moodSmallObject = moodSmallObject;
        this.moodBigObject = moodBigObject;
    }
}
