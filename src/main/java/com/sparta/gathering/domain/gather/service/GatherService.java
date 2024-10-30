package com.sparta.gathering.domain.gather.service;

import com.sparta.gathering.domain.gather.dto.request.GatherRequest;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.user.dto.response.UserDTO;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GatherService {

    void createGather(GatherRequest request, UserDTO userDto, UUID categoryId);

    void modifyGather(GatherRequest request, Long id, UserDTO userDto);

    void deleteGather(Long id, UserDTO userDto);

    Page<Gather> gathers(Pageable pageable, UUID categoryId);

    Page<Gather> findTitle(Pageable pageable, String keyword);

}
