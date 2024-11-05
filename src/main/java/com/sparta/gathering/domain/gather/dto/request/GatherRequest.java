package com.sparta.gathering.domain.gather.dto.request;

import com.sparta.gathering.domain.map.entity.Map;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GatherRequest {

    @NotNull(message = "모임명을 입력해주세요")
    private String title;
    @NotNull(message = "설명을 입력해 주세요")
    private String description;
    @NotNull(message = "설명을 입력해 주세요")
    private List<String> hashtags;
    @NotNull(message ="위치를 확인해주세요")
    private String addressName;
    @NotNull(message ="위치를 확인해주세요")
    private Double latitude;//위도 y
    @NotNull(message ="위치를 확인해주세요")
    private Double longitude;//경도 x
}
