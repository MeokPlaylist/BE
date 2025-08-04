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

    public UserCategory() {

    }
}
