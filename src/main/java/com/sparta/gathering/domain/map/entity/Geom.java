package com.sparta.gathering.domain.map.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;


@Entity
@NoArgsConstructor
@Getter
@Table(name = "geom")
public class Geom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String addressName;

    @Column
    private Double longitude;

    @Column
    private Double latitude;

    @Column
    private Point location;


}
