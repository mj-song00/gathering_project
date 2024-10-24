package com.sparta.gathering.domain.gather.service;

import com.sparta.gathering.domain.gather.dto.request.GatherRequest;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.user.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface GatherService {
    void createGather(GatherRequest request, User user);
    void modifyGather(GatherRequest request, Long id, User user);

    void deleteGather(Long id, User user);

    List<Gather> Gathers(Pageable pageable);
}
