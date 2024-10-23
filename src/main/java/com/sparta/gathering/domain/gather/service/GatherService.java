package com.sparta.gathering.domain.gather.service;

import com.sparta.gathering.domain.gather.dto.request.GatherRequest;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.user.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GatherService {
    void createGather(GatherRequest request, User user);
    void modifyGather(GatherRequest request, long id, User user);

    void deleteGather(long id, User user);

    List<Gather> Gathers(Pageable pageable);
}
