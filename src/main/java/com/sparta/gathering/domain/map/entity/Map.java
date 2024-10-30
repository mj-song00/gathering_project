package com.sparta.gathering.domain.map.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    private String latitude;//위도 y

    @Column
    private String longitude;//경도 x


    public Map(String addressName){
        this.addressName = addressName;
    }
    public Map(String addressName, String latitude, String longitude){
        this.addressName = addressName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
