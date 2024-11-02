package com.sparta.gathering.domain.gather.repository;

import com.sparta.gathering.domain.gather.dto.response.GatherListResponseItem;

public interface GatherCustomRepository {
    GatherListResponseItem findByIdWithBoardAndSchedule(Long gatherId);
}
