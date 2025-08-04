package com.meokplaylist.infra.Category;

import com.meokplaylist.infra.Users;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

public class UserLocalCategory {
    @EmbeddedId
    private UserLocalCategory id;

    @ManyToOne
    @MapsId("userId") // 복합키 매핑
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private LocalCategory localCategory;

    public UserLocalCategory( LocalCategory localCategory,Users user) {
        this.localCategory = localCategory;
        this.user = user;
    }
}
