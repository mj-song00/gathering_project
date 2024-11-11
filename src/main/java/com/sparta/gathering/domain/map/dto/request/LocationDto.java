package com.sparta.gathering.domain.map.dto.request;

import lombok.Data;

@Data
public class LocationDto {
    private String name;
    private Double lat;
    private Double lng;
}