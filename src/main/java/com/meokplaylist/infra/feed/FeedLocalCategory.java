package com.meokplaylist.infra.feed;

import com.meokplaylist.infra.category.LocalCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feed_local_category",
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_category_id")
    private LocalCategory localCategory;

    //JPA가 반드시 요구하는 기본 생성자 — Lombok 쓰지 말고 명시적으로 추가
    protected FeedLocalCategory() {
    }

    public FeedLocalCategory(LocalCategory localCategory, Feed feed) {
        this.localCategory = localCategory;
        this.feed = feed;
    }
}
