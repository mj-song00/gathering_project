package com.sparta.gathering.domain.gather.dto.response;

import com.sparta.gathering.domain.gather.entity.Gather;
import lombok.Getter;

@Getter
public class SearchResponseItem {
    private final Long id; // Gather ID
    private final String title; // Gather 제목
    private final String description;

    public SearchResponseItem(Gather gather) {
        this.id = gather.getId();
        this.title = gather.getTitle();
        this.description = gather.getDescription();
    }
}
