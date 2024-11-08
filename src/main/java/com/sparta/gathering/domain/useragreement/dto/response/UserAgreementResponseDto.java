package com.sparta.gathering.domain.useragreement.dto.response;

import com.sparta.gathering.domain.agreement.enums.AgreementStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserAgreementResponseDto {

    private UUID userId;
    private UUID agreementId;
    private AgreementStatus status;
    private LocalDateTime agreedAt;
}
