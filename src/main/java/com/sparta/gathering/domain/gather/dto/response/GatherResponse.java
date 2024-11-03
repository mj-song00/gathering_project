package com.sparta.gathering.domain.gather.dto.response;

import com.sparta.gathering.domain.board.entity.Board;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.hashtag.entity.HashTag;
import com.sparta.gathering.domain.schedule.entity.Schedule;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GatherResponse {

    private final Long id; // Gather ID
    private final String title; // Gather 제목
    private final String description;
    private final List<String> hashtags;
    private final List<Long> scheduleIds;
    private final List<String> schedule;
    private final List<Long> boardIds;
    private final List<String> board;
    private final String category;
    private final String address;


    public GatherResponse(Gather gather) {
        this.id = gather.getId(); // Gather ID
        this.title = gather.getTitle(); // Gather 제목
        this.description = gather.getDescription();
        this.hashtags = gather.getHashTagList().stream()
                .map(HashTag::getHashTagName)
                .collect(Collectors.toList());
        this.scheduleIds = gather.getScheduleList().isEmpty() ? null
                : gather.getScheduleList().stream()
                .map(Schedule::getId)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        this.schedule = gather.getScheduleList().stream()
                .map(Schedule::getScheduleTitle).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        this.boardIds = gather.getBoardList().isEmpty() ? null
                : gather.getBoardList().stream()
                .map(Board::getId)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        this.board = gather.getBoardList().stream()
                .map(Board::getBoardTitle).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        this.category = gather.getCategory().getCategoryName();
        this.address = gather.getMap().getAddressName();
    }
}