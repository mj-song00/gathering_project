package com.sparta.gathering.domain.gather.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.domain.gather.dto.request.GatherRequest;
import com.sparta.gathering.domain.gather.dto.response.GatherListResponse;
import com.sparta.gathering.domain.gather.dto.response.GatherResponse;
import com.sparta.gathering.domain.gather.dto.response.NewGatherResponse;
import com.sparta.gathering.domain.gather.dto.response.RankResponse;
import com.sparta.gathering.domain.gather.entity.Gather;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GatherService {

    void createGather(GatherRequest request, AuthenticatedUser authenticatedUser, Long categoryId);

    void modifyGather(GatherRequest request, Long id, AuthenticatedUser authenticatedUser);

    void deleteGather(Long id, AuthenticatedUser authenticatedUser);

    Page<Gather> gathers(Pageable pageable, Long categoryId);

    Page<Gather> findByHashTags(Pageable pageable, List<String> hashTagName);

    List<RankResponse> getTop5Ranking();

    GatherResponse getDetails(Long gatherId);

    List<NewGatherResponse> newCreatedGatherList();

    Page<Gather> findByTitles(Pageable pageable, String title);

    List<GatherListResponse> getGatherList(AuthenticatedUser authenticatedUser);
}