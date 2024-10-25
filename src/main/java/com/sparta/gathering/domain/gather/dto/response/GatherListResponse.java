package com.sparta.gathering.domain.gather.dto.response;

import com.sparta.gathering.domain.gather.entity.Gather;
import lombok.Getter;


@Getter
public class GatherListResponse {
    private String title;

    public GatherListResponse(Gather gather){
        this.title = gather.getTitle();
    }
}

