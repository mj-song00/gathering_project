package com.sparta.gathering.domain.schedule.repository;

import com.sparta.gathering.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
}
