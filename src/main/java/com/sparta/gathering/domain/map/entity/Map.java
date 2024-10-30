package com.sparta.gathering.domain.map.entity;

import com.fasterxml.jackson.databind.JsonNode;
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
    private double latitude;//위도 y

    @Column
    private double longitude;//경도 x

    public Map(String addressName){
        this.addressName = addressName;
//        this.latitude = node.path("y").asDouble();
//        this.longitude = node.path("x").asDouble();
    }
}
