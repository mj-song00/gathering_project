package com.sparta.gathering.domain.schedule.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.schedule.dto.request.ScheduleRequestDto;
import com.sparta.gathering.domain.schedule.dto.response.ScheduleResponseDto;
import com.sparta.gathering.domain.schedule.entity.Schedule;
import com.sparta.gathering.domain.schedule.repository.ScheduleRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final GatherRepository gatherRepository;

    @Transactional
    public ScheduleResponseDto createSchedule(Long gatherId,
            ScheduleRequestDto scheduleRequestDto) {
        // gatherId로 Gather 엔티티 조회
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));

        // Schedule 엔티티 생성 및 Gather 엔티티 설정
        Schedule schedule = new Schedule(scheduleRequestDto.getScheduleTitle(),
                scheduleRequestDto.getScheduleContent(), gather);

        // Gather의 scheduleList에 새로운 Schedule 추가
        gather.getScheduleList().add(schedule); // 양방향 연관관계 설정
        scheduleRepository.save(schedule);

        // ScheduleResponseDto로 변환하여 반환
        return toResponseDto(schedule);
    }

    @Transactional
    public ScheduleResponseDto updateSchedule(Long gatherId, Long scheduleId,
            ScheduleRequestDto scheduleRequestDto) {
        // gatherId로 Gather 엔티티 조회
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));

        // gather의 scheduleList에서 scheduleId와 일치하는 Schedule을 찾음
        Schedule schedule = gather.getScheduleList().stream()
                .filter(s -> s.getId().equals(scheduleId))
                .findFirst()
                .orElseThrow(() -> new BaseException(ExceptionEnum.SCHEDULE_NOT_FOUND));

        // 스케줄 내용 업데이트
        schedule.update(scheduleRequestDto.getScheduleTitle(),
                scheduleRequestDto.getScheduleContent(), gather);

        // 변경된 스케줄 저장
        scheduleRepository.save(schedule);  // 저장 후

        // ScheduleResponseDto로 변환하여 반환
        return toResponseDto(schedule);
    }

    @Transactional
    public void deleteSchedule(Long gatherId, Long scheduleId) {
        // gatherId로 Gather 엔티티 조회
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));

        // gather의 scheduleList에서 scheduleId와 일치하는 Schedule을 찾음
        Schedule schedule = gather.getScheduleList().stream()
                .filter(s -> s.getId().equals(scheduleId))
                .findFirst()
                .orElseThrow(() -> new BaseException(ExceptionEnum.SCHEDULE_NOT_FOUND));

        // gather의 scheduleList에서 스케줄을 제거
        gather.getScheduleList().remove(schedule);

        schedule.delete(LocalDateTime.now());

        // 스케줄 삭제
        scheduleRepository.delete(schedule);
    }

    // Schedule을 ScheduleResponseDto로 변환하는 메소드 추가
    private ScheduleResponseDto toResponseDto(Schedule schedule) {
        ScheduleResponseDto responseDto = new ScheduleResponseDto();
        responseDto.setScheduleId(schedule.getId());
        responseDto.setScheduleTitle(schedule.getScheduleTitle());
        responseDto.setScheduleContent(schedule.getScheduleContent());
        return responseDto;
    }
}
