package com.sparta.gathering.domain.agreement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AgreementUpdateRequestDto {

    @NotBlank(message = "약관 내용은 필수 입력 값입니다.")
    private String content;

}
