package com.sparta.gathering.domain.schedule.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.schedule.dto.request.ScheduleRequestDto;
import com.sparta.gathering.domain.schedule.dto.response.ScheduleResponseDto; // 추가된 import
import com.sparta.gathering.domain.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; // 추가된 import
import org.springframework.web.bind.annotation.*;

@RestController // @Controller에서 @RestController로 변경
@RequiredArgsConstructor
@RequestMapping("/api/gather/{gatherId}/schedule")
@Tag(name = "Schedule API", description = "스케줄 API")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping
    @Operation(summary = "Create Schedule", description = "스케줄 생성")
    public ResponseEntity<ApiResponse<ScheduleResponseDto>> createSchedule(
            @PathVariable(name = "gatherId") Long gatherId,
            @RequestBody ScheduleRequestDto scheduleRequestDto)
    {
        ScheduleResponseDto scheduleResponseDto = scheduleService.createSchedule(gatherId, scheduleRequestDto);
        return ResponseEntity.ok(ApiResponse.successWithData(scheduleResponseDto, ApiResponseEnum.SCHEDULE_CREATED));
    }

    @PutMapping("/{scheduleId}")
    @Operation(summary = "Update Schedule", description = "스케줄 수정")
    public ResponseEntity<ApiResponse<ScheduleResponseDto>> updateSchedule(
            @PathVariable(name = "gatherId") Long gatherId,
            @PathVariable(name = "scheduleId") Long scheduleId,
            @RequestBody ScheduleRequestDto scheduleRequestDto)
    {
        ScheduleResponseDto updatedSchedule = scheduleService.updateSchedule(gatherId, scheduleId, scheduleRequestDto);
        return ResponseEntity.ok(ApiResponse.successWithData(updatedSchedule, ApiResponseEnum.SCHEDULE_UPDATED));
    }

    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "Delete Schedule", description = "스케줄 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @PathVariable(name = "gatherId") Long gatherId,
            @PathVariable(name = "scheduleId") Long scheduleId)
    {
        scheduleService.deleteSchedule(gatherId, scheduleId);
        return ResponseEntity.ok(ApiResponse.successWithData(null, ApiResponseEnum.SCHEDULE_DELETED));
    }
}
