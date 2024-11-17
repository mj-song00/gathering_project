package com.sparta.gathering.domain.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryReq {

    @NotBlank(message = "카테고리명을 입력해주세요.")
    private String categoryName;
}
