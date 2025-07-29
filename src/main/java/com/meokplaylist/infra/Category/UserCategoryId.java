package com.meokplaylist.infra.Category;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCategoryId implements Serializable {

    private Long user;
    private Long category;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCategoryId)) return false;
        UserCategoryId that = (UserCategoryId) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, category);
    }
}
