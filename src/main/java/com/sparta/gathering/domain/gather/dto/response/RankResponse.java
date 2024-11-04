package com.sparta.gathering.domain.gather.dto.response;

import lombok.Getter;

@Getter
public class RankResponse {
    private double score;
    private String adress;

    public RankResponse(double score, String value) {
        this.score = score;
        this.adress = value;
    }
}
