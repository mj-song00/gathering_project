package com.sparta.gathering.domain.gather.dto.response;

import com.sparta.gathering.domain.gather.entity.Gather;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NewGatherResponse {

    private final String title;

    public static NewGatherResponse from(Gather gather) {
        return new NewGatherResponse(gather.getTitle());
    }


}

