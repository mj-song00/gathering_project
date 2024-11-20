package com.sparta.gathering.domain.map.entity;

import com.sparta.gathering.domain.gather.entity.Gather;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "map")
@Builder
@AllArgsConstructor
@Setter
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
    private Gather gather;


    public Map(String addressName, Double latitude, Double longitude) {
        this.addressName = addressName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void saveGather(Gather gather) {
        this.gather = gather;
    }
}
