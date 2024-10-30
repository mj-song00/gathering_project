package com.sparta.gathering.domain.gather.service;

import com.sparta.gathering.domain.gather.dto.request.GatherRequest;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.user.dto.response.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GatherService {

    void createGather(GatherRequest request, UserDTO userDto, Long categoryId);

    void modifyGather(GatherRequest request, Long id, UserDTO userDto);

    void deleteGather(Long id, UserDTO userDto);

    Page<Gather> gathers(Pageable pageable, Long categoryId);

    Page<Gather> findTitle(Pageable pageable, String keyword);

}
