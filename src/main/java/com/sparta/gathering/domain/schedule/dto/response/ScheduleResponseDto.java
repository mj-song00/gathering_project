package com.sparta.gathering.domain.schedule.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleResponseDto {
    private Long scheduleId;
    private String scheduleTitle;
    public String scheduleContent;
}
