package com.sparta.gathering.domain.gather.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GatherRequest {

  @NotNull(message = "모임명을 입력해주세요")
  private String title;
  @NotNull(message = "설명을 입력해 주세요")
  private String description;
  // @NotNull(message="해시태그를 입력해 주세요")
  private List<String> hashtags;
}
