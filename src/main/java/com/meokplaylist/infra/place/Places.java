package com.meokplaylist.infra.place;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Places {

    @Id
    private Long id;

    private String name;

    private String addressName; //지번 주소

    private String roadAddressName; //도로명 주소

    private String kakaoMapUrl; //지도랑 mapping 되는 url 일듯

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    private String phone;

    private String kakaoCategoryGroup; //음식점, 카페 -> 카카오에서 재공하는것
    private String kakaoCategoryName; //음식점 -> 한식 -> 김밥 //해봐야 어떻게 나오는지 알듯



    public Places(Long id,String name, String addressName, String roadAddressName, String kakaoMapUrl, String phone, String kakaoCategoryGroup, String kakaoCategoryName) {
        this.id=id;
        this.name = name;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.kakaoMapUrl = kakaoMapUrl;
        this.phone = phone;
        this.kakaoCategoryGroup = kakaoCategoryGroup;
        this.kakaoCategoryName = kakaoCategoryName;
    }
}
