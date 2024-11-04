package com.sparta.gathering.domain.map.entity;

import com.sparta.gathering.domain.gather.entity.Gather;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @OneToOne
    @JoinColumn(name = "gather_id")
    private Gather gather;


    public Map(String addressName, Double latitude, Double longitude,Gather gather) {
        this.addressName = addressName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.gather = gather;
    }

}
