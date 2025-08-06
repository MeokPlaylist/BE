package com.meokplaylist.infra.feed;

import com.meokplaylist.infra.category.Category;
import com.meokplaylist.infra.category.LocalCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class FeedCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private FeedCategory id;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "local_category_id")
    private LocalCategory localCategory;

    public FeedCategory(Category category, Feed feed) {
        this.feed = feed;
        this.category = category;
    }

    public FeedCategory(Feed feed, Category category, LocalCategory localCategory) {
        this.feed = feed;
        this.category = category;
        this.localCategory = localCategory;
    }
}
