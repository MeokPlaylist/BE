package com.meokplaylist.infra.feed;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    @JsonIgnore  //임시방편으로 Json 직렬화를 위해  선언 원래라면 엔터티 자체를 반환하지 않고 새로 dto 만들어야 함
    private Feed feed;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


    public FeedCategory(Category category, Feed feed) {
        this.feed = feed;
        this.category = category;
    }

    public FeedCategory(Feed feed, Category category, LocalCategory localCategory) {
        this.feed = feed;
        this.category = category;
    }
}
