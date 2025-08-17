package com.meokplaylist.infra.category;

import com.meokplaylist.infra.Users;
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
public class UserCategory {

    @EmbeddedId
    private UserCategoryId id;

    @ManyToOne
    @MapsId("userId") // 복합키 매핑
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "local_category_id")
    private LocalCategory localCategory;

    public UserCategory(Category category, Users user) {
        this.category = category;
        this.user = user;
        this.id = new UserCategoryId(user.getUserId(), category.getCategoryId());
    }

    public UserCategory(Users user, Category category, LocalCategory localCategory) {
        this.user = user;
        this.category = category;
        this.localCategory = localCategory;
        this.id = new UserCategoryId(user.getUserId(), category.getCategoryId());
    }
}
