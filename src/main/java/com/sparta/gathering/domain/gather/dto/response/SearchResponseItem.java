package com.sparta.gathering.domain.gather.dto.response;

import com.sparta.gathering.domain.gather.entity.Gather;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SearchResponseItem {
    private final Long id; // Gather ID
    private final String title; // Gather 제목
    private final String description;
    private final List<String> hashtags;

    public SearchResponseItem(Gather gather) {
        this.id = gather.getId();
        this.title = gather.getTitle();
        this.description = gather.getDescription();
        this.hashtags = gather.getGatherHashtags().stream()
                .map(gatherHashtag -> gatherHashtag.getHashTag().getHashTagName())
                .collect(Collectors.toList());
    }
}
