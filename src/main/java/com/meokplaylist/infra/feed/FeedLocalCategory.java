package com.meokplaylist.infra.feed;

import com.meokplaylist.infra.category.LocalCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(  name = "feed_local_category",
        indexes = {
                @Index(name = "idx_flc_feed", columnList = "feed_id"),
                @Index(name = "idx_flc_local", columnList = "local_category_id")
})
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
