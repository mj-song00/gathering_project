package com.sparta.gathering.domain.schedule.dto.response;

import com.sparta.gathering.domain.schedule.entity.Schedule;
import lombok.Getter;

@Getter
public class ScheduleSimpleResponseDto {
    private final String scheduleTitle;

    public ScheduleSimpleResponseDto(Schedule schedule){
        this.scheduleTitle = schedule.getScheduleTitle();
    }
}
