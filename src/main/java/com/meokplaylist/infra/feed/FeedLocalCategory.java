package com.meokplaylist.infra.feed;

import com.meokplaylist.infra.category.LocalCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class FeedLocalCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @ManyToOne
    @JoinColumn(name = "local_category_id")
    private LocalCategory localCategory;

    public FeedLocalCategory( LocalCategory localCategory,Feed feed) {
        this.localCategory = localCategory;
        this.feed=feed;
    }
}
