package com.sparta.gathering.domain.gather.dto.response;


import com.sparta.gathering.domain.gather.entity.Gather;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GatherListResponse {
    private final Long id;
    private final String title;

    public static GatherListResponse from(Gather gather) {
        return new GatherListResponse(gather.getId(), gather.getTitle());
    }
}
