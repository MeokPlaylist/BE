package com.meokplaylist.infra.feed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meokplaylist.infra.category.Category;
import com.meokplaylist.infra.category.LocalCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FeedCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


    public FeedCategory(Category category, Feed feed) {
        this.feed = feed;
        this.category = category;
    }
}
