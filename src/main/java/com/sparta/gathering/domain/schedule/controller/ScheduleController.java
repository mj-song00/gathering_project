package com.sparta.gathering.domain.schedule.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.schedule.dto.request.ScheduleRequestDto;
import com.sparta.gathering.domain.schedule.entity.Schedule;
import com.sparta.gathering.domain.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/{gatherId}/schedule")
@Tag(name = "Schedule API", description = "스케줄 API")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    @Operation(summary = "Create Schedule", description = "스케줄 생성")
    public ApiResponse<?> createSchedule(
            @PathVariable(name = "gatherId") Long gatherId,
            @RequestBody ScheduleRequestDto scheduleRequestDto) {
        Schedule schedule = scheduleService.createSchedule(gatherId, scheduleRequestDto);
        return ApiResponse.successWithData(schedule, ApiResponseEnum.SCHEDULE_CREATED);
    }

    @PutMapping("/{scheduleId}")
    @Operation(summary = "Update Schedule", description = "스케줄 수정")
    public ApiResponse<?> updateSchedule(
            @PathVariable(name = "gatherId") Long gatherId,
            @PathVariable(name = "scheduleId") Long scheduleId,
            @RequestBody ScheduleRequestDto scheduleRequestDto) {
        Schedule updatedSchedule = scheduleService.updateSchedule(gatherId, scheduleId,
                scheduleRequestDto);
        return ApiResponse.successWithData(updatedSchedule, ApiResponseEnum.SCHEDULE_UPDATED);
    }

    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "Delete Schedule", description = "스케줄 삭제")
    public ApiResponse<?> deleteSchedule(
            @PathVariable(name = "gatherId") Long gatherId,
            @PathVariable(name = "scheduleId") Long scheduleId) {
        scheduleService.deleteSchedule(gatherId, scheduleId);
        return ApiResponse.successWithData(null, ApiResponseEnum.SCHEDULE_DELETED);
    }
}

