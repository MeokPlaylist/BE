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
public class UserLocalCategory {

    @EmbeddedId
    private UserLocalCategoryId id;

    @ManyToOne
    @MapsId("userId") // 복합키 매핑
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "local_category_id")
    private LocalCategory localCategory;

    public UserLocalCategory( LocalCategory localCategory,Users user) {
        this.localCategory = localCategory;
        this.user = user;
        this.id=new UserLocalCategoryId(user.getUserId(),localCategory.getLocalCategoryId());
    }
}
