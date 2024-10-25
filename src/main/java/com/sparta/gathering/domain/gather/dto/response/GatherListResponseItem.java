package com.sparta.gathering.domain.gather.dto.response;

import com.sparta.gathering.domain.gather.entity.Gather;
import lombok.Getter;

@Getter
public class GatherListResponseItem {
    private Long id; // Gather ID
    private String title; // Gather 제목

    public GatherListResponseItem(Gather gather) {
        this.id = gather.getId(); // Gather ID
        this.title = gather.getTitle(); // Gather 제목
    }
}