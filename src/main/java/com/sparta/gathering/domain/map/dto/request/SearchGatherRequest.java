package com.sparta.gathering.domain.map.dto.request;

import lombok.Getter;

@Getter
public class SearchGatherRequest {

    private Double latitude; //위도 y
    private Double longitude; //경도 x
    private Integer distance; //거리
}
