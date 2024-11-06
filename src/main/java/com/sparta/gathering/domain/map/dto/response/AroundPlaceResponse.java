package com.sparta.gathering.domain.map.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AroundPlaceResponse {

    private String address; //주소 이름

    private Double longitude; //x 경도
    private Double latitude;  //y 위도
}
