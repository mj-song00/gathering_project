package com.sparta.gathering.domain.useragreement.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserAgreementRequestDto {

    @NotNull(message = "사용자 ID는 필수 입력 값입니다.")
    private UUID userId;

    @NotNull(message = "약관 ID는 필수 입력 값입니다.")
    private UUID agreementId;
}
