package com.sparta.gathering.domain.schedule.controller;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.schedule.dto.request.ScheduleRequestDto;
import com.sparta.gathering.domain.schedule.dto.response.ScheduleResponseDto; // 추가된 import
import com.sparta.gathering.domain.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; // 추가된 import
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController // @Controller에서 @RestController로 변경
@RequiredArgsConstructor
@RequestMapping("/api/gathers/{gatherId}/schedules")
@Tag(name = "Schedule API", description = "스케줄 API")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping
    @Operation(summary = "스케쥴 생성", description = "스케줄 생성")
    public ResponseEntity<ApiResponse<ScheduleResponseDto>> createSchedule(
            @PathVariable(name = "gatherId") Long gatherId,
            @RequestBody ScheduleRequestDto scheduleRequestDto,
            @AuthenticationPrincipal AuthenticatedUser authUser)
    {
        ScheduleResponseDto scheduleResponseDto = scheduleService.createSchedule(gatherId, scheduleRequestDto, authUser);
        return ResponseEntity.ok(ApiResponse.successWithData(scheduleResponseDto, ApiResponseEnum.SCHEDULE_CREATED));
    }

    @PutMapping("/{scheduleId}")
    @Operation(summary = "스케쥴 수정", description = "스케줄 수정")
    public ResponseEntity<ApiResponse<ScheduleResponseDto>> updateSchedule(
            @PathVariable(name = "gatherId") Long gatherId,
            @PathVariable(name = "scheduleId") Long scheduleId,
            @RequestBody ScheduleRequestDto scheduleRequestDto,
            @AuthenticationPrincipal AuthenticatedUser authUser)
    {
        ScheduleResponseDto updatedSchedule = scheduleService.updateSchedule(gatherId, scheduleId, scheduleRequestDto, authUser);
        return ResponseEntity.ok(ApiResponse.successWithData(updatedSchedule, ApiResponseEnum.SCHEDULE_UPDATED));
    }

    @PatchMapping("/{scheduleId}/delete")
    @Operation(summary = "스케쥴 삭제", description = "스케줄 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @PathVariable(name = "gatherId") Long gatherId,
            @PathVariable(name = "scheduleId") Long scheduleId,
            @AuthenticationPrincipal AuthenticatedUser authUser)
    {
        scheduleService.deleteSchedule(gatherId, scheduleId, authUser);
        return ResponseEntity.ok(ApiResponse.successWithData(null, ApiResponseEnum.SCHEDULE_DELETED));
    }
}

