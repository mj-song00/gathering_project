package com.sparta.gathering.domain.gather.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GatherRequest {
    @NotNull(message="모임명을 입력해주세요")
    private String title;
}
