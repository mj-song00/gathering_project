package com.sparta.gathering.domain.gather.repository;

import com.sparta.gathering.domain.gather.dto.response.GatherResponse;
import com.sparta.gathering.domain.gather.entity.Gather;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GatherCustomRepository {
    GatherResponse findByIdWithBoardAndSchedule(Long gatherId);

    Page<Gather> findByKeywords(Pageable pageable, List<String> hashTagName);
}
