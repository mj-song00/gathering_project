package com.sparta.gathering.domain.schedule.service;

import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.schedule.dto.request.ScheduleRequestDto;
import com.sparta.gathering.domain.schedule.entity.Schedule;
import com.sparta.gathering.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final GatherRepository gatherRepository;

    public Schedule createSchedule(Long gatherId, ScheduleRequestDto scheduleRequestDto) {
        // gatherId로 Gather 엔티티 조회
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임을 찾을 수 없습니다. gatherId: " + gatherId));

        // Schedule 엔티티 생성 및 Gather 엔티티 설정
        Schedule schedule = new Schedule(scheduleRequestDto.getScheduleTitle(), scheduleRequestDto.getScheduleContent());
        schedule.setGather(gather);  // Schedule에 Gather 엔티티 설정

        // Schedule 저장
        return scheduleRepository.save(schedule);
    }

    public Schedule updateSchedule(Long gatherId, Long scheduleId, ScheduleRequestDto scheduleRequestDto) {
        // gatherId로 Gather 엔티티 조회
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임을 찾을 수 없습니다. gatherId: " + gatherId));

        // gather의 scheduleList에서 scheduleId와 일치하는 Schedule을 찾음
        Schedule schedule = gather.getScheduleList().stream()
                .filter(s -> s.getId().equals(scheduleId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 스케줄을 찾을 수 없습니다. scheduleId: " + scheduleId));

        // 스케줄 내용 업데이트
        schedule.update(scheduleRequestDto.getScheduleTitle(), scheduleRequestDto.getScheduleContent());

        // 변경된 스케줄 저장
        return scheduleRepository.save(schedule);  // 저장 후 바로 반환
    }

    public void deleteSchedule(Long gatherId, Long scheduleId) {
        // gatherId로 Gather 엔티티 조회
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임을 찾을 수 없습니다. gatherId: " + gatherId));

        // gather의 scheduleList에서 scheduleId와 일치하는 Schedule을 찾음
        Schedule schedule = gather.getScheduleList().stream()
                .filter(s -> s.getId().equals(scheduleId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 스케줄을 찾을 수 없습니다. scheduleId: " + scheduleId));

        // gather의 scheduleList에서 스케줄을 제거
        gather.getScheduleList().remove(schedule);

        // 스케줄 삭제
        scheduleRepository.delete(schedule);
    }
}

