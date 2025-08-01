package com.meokplaylist.infra.Category;

import com.meokplaylist.infra.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class UserCategory {

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_category_id", nullable = false)
    private FoodCategory foodCategory;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_category_id")
    private LocalCategory localCategory;

    public UserCategory(Users user, FoodCategory foodCategory) {
        this.user = user;
        this.foodCategory = foodCategory;
    }

    public UserCategory(Users user, LocalCategory localCategory, FoodCategory foodCategory) {
        this.user = user;
        this.localCategory = localCategory;
        this.foodCategory = foodCategory;
    }
}
