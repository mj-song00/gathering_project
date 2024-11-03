package com.sparta.gathering.domain.gather.repository;

import com.sparta.gathering.domain.gather.dto.response.GatherResponse;

public interface GatherCustomRepository {
    GatherResponse findByIdWithBoardAndSchedule(Long gatherId);
}
