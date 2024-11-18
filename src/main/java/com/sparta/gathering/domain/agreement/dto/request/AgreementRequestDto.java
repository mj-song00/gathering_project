package com.sparta.gathering.domain.agreement.dto.request;

import com.sparta.gathering.domain.agreement.enums.AgreementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AgreementRequestDto {

    private UUID agreementId;

    @NotBlank(message = "약관 내용은 필수 입력 값입니다.")
    private String content;

    @NotBlank(message = "약관 버전 정보는 필수 입력 값입니다.")
    private String version;

    @NotNull(message = "약관 유형이 누락되었습니다.")
    private AgreementType type;

}
