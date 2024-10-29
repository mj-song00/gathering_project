package com.sparta.gathering.domain.gather.dto.response;

import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.hashtag.entity.HashTag;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GatherListResponseItem {

    private final Long id; // Gather ID
    private final String title; // Gather 제목
    private final String description;
    private final List<String> hashtags;
    //    private final List<String> schedule;
//    private final List<String> board;
    private final String category;

    public GatherListResponseItem(Gather gather) {
        this.id = gather.getId(); // Gather ID
        this.title = gather.getTitle(); // Gather 제목
        this.description = gather.getDescription();
        this.hashtags = gather.getHashTagList().stream()
                .map(HashTag::getHashTagName)
                .collect(Collectors.toList());
//        this.schedule = gather.getScheduleList().stream().map(Schedule::getScheduleTitle).collect(Collectors.toList());
//        this.board = gather.getBoardList().stream().map(Board::getBoardTitle).collect(Collectors.toList());
        this.category = gather.getCategory().getCategoryName();
    }
}