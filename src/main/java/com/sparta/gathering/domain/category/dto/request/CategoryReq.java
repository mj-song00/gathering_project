package com.sparta.gathering.domain.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CategoryReq {

    @NotBlank(message = "카테고리명을 입력해주세요.")
    private String title;
}
