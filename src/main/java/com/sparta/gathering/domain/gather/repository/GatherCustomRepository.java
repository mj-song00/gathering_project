package com.sparta.gathering.domain.gather.repository;

import com.sparta.gathering.domain.gather.dto.response.GatherResponse;
import com.sparta.gathering.domain.gather.entity.Gather;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GatherCustomRepository {
    Optional<Gather> findByIdWithBoardAndSchedule(Long gatherId);

    Page<Gather> findByKeywords(Pageable pageable, List<String> hashTagName);
}
