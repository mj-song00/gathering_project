package com.sparta.gathering.domain.gather.service;

import com.sparta.gathering.domain.gather.dto.request.GatherRequest;
import com.sparta.gathering.domain.gather.entity.Gather;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GatherService {
    void createGather(GatherRequest request);
    void modifyGather(GatherRequest request, long id);

    void deleteGather(long id);

    List<Gather> Gathers(Pageable pageable);
}
