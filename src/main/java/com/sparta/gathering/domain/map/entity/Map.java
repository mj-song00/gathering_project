package com.sparta.gathering.domain.map.entity;

import com.sparta.gathering.domain.gather.entity.Gather;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "map")
public class Map {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String addressName;

    @Column
    private Double latitude;//위도 y

    @Column
    private Double longitude;//경도 x

//    @Column(columnDefinition = "POINT SRID 4326", nullable = false)
//    private Point point;

    @OneToOne
    @JoinColumn(name = "gather_id")
    private Gather gather;


    public Map(String addressName, Double latitude, Double longitude) {
        this.addressName = addressName;
        this.latitude = latitude;
        this.longitude = longitude;
//        this.point = point;
    }

    public void saveGather(Gather gather) {
        this.gather = gather;
        if (gather.getMap() != this) {
            gather.saveMap(this); // 양방향 관계 설정
        }
    }
}
