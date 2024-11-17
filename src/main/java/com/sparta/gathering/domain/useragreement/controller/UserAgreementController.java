package com.sparta.gathering.domain.useragreement.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.common.service.ReagreeOneTimeTokenService;
import com.sparta.gathering.domain.agreement.enums.AgreementStatus;
import com.sparta.gathering.domain.useragreement.dto.response.UserAgreementResponseDto;
import com.sparta.gathering.domain.useragreement.service.UserAgreementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "UserAgreement", description = "사용자 약관 동의 API")
@RequestMapping("/api/user-agreements")
@RequiredArgsConstructor
public class UserAgreementController {

    private final UserAgreementService userAgreementService;
    private final ReagreeOneTimeTokenService reagreeOneTimeTokenService;

    @Operation(summary = "약관 동의 상태 조회", description = "사용자의 약관 동의 상태를 조회합니다.")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<UserAgreementResponseDto>> getUserAgreementStatus(
            @RequestParam UUID userId,
            @RequestParam UUID agreementId) {
        UserAgreementResponseDto response = userAgreementService.getUserAgreementStatus(userId, agreementId);
        return ResponseEntity.ok(
                ApiResponse.successWithData(response, ApiResponseEnum.AGREEMENT_STATUS_RETRIEVED_SUCCESS));
    }

    @Operation(summary = "약관 상태별 조회", description = "특정 상태의 약관을 조회합니다.")
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<UserAgreementResponseDto>>> getUserAgreementsByStatus(
            @PathVariable AgreementStatus status) {
        List<UserAgreementResponseDto> responseDto = userAgreementService.getUserAgreementsByStatus(status);
        return ResponseEntity.ok(
                ApiResponse.successWithData(responseDto, ApiResponseEnum.AGREEMENT_STATUS_RETRIEVED_SUCCESS));
    }

    @Operation(summary = "약관 재동의", description = "사용자가 약관 변경에 재동의합니다.")
    @PatchMapping("/reagree")
    public ResponseEntity<ApiResponse<Void>> reAgreeToTerms(@RequestParam String token) {
        // 토큰 검증 및 사용자 데이터 추출
        ReagreeOneTimeTokenService.TokenData tokenData = reagreeOneTimeTokenService.validateAndUseToken(token);

        // 비즈니스 로직 처리
        userAgreementService.reAgreeToTerms(tokenData.getUserId(), tokenData.getAgreementId());

        return ResponseEntity.ok(ApiResponse.successWithOutData(ApiResponseEnum.AGREEMENT_REAGREED_SUCCESS));
    }

}

