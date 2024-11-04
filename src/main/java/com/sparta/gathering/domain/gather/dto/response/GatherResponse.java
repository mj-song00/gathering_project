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
    private final String category;
    private final String address;

    @Getter
    public static class BoardInfo {
        private final Long id;
        private final String title;
        private final String boardContents;

        public BoardInfo(Long id, String title, String boardContents) {
            this.id = id;
            this.title = title;
            this.boardContents = boardContents;
        }
    }

    public final List<BoardInfo> boardInfos;

    @Getter
    public static class ScheduleInfo {
        private final Long id;
        private final String title;
        private final String scheduleContents;

        public ScheduleInfo(Long id, String title, String scheduleContents) {
            this.id = id;
            this.title = title;
            this.scheduleContents = scheduleContents;
        }
    }

    public final List<ScheduleInfo> scheduleInfos;

    public GatherResponse(Gather gather) {
        this.id = gather.getId(); // Gather ID
        this.title = gather.getTitle(); // Gather 제목
        this.description = gather.getDescription();
        this.hashtags = gather.getHashTagList().stream().map(HashTag::getHashTagName).collect(Collectors.toList());
        this.scheduleInfos = gather.getScheduleList().isEmpty() ? null :
                gather.getScheduleList().stream()
                        .map(schedule -> new ScheduleInfo(schedule.getId(), schedule.getScheduleTitle(), schedule.getScheduleContent())).sorted(Comparator.comparing(ScheduleInfo::getId).reversed()) // ID 기준 내림차순 정렬
                        .collect(Collectors.toList());
        this.boardInfos = gather.getBoardList().isEmpty() ? null : gather.getBoardList().stream().map(board -> new BoardInfo(board.getId(), board.getBoardTitle(), board.getBoardContent())).sorted(Comparator.comparing(BoardInfo::getId).reversed()) // ID 기준 내림차순 정렬
                .collect(Collectors.toList());
        this.category = gather.getCategory().getCategoryName();
        this.address = gather.getMap().getAddressName();
    }
}