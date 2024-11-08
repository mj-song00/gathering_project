package com.sparta.gathering.domain.useragreement.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.useragreement.dto.request.UserAgreementRequestDto;
import com.sparta.gathering.domain.useragreement.dto.response.UserAgreementResponseDto;
import com.sparta.gathering.domain.useragreement.service.UserAgreementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "UserAgreement", description = "사용자 약관 동의 API")
@RequestMapping("/api/user-agreements")
@RequiredArgsConstructor
public class UserAgreementController {

    private final UserAgreementService userAgreementService;

    @Operation(summary = "약관 동의 상태 조회", description = "사용자의 약관 동의 상태를 조회합니다.")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<UserAgreementResponseDto>> getUserAgreementStatus(
            @RequestParam UUID userId,
            @RequestParam UUID agreementId) {
        UserAgreementResponseDto response = userAgreementService.getUserAgreementStatus(userId, agreementId);
        return ResponseEntity.ok(
                ApiResponse.successWithData(response, ApiResponseEnum.AGREEMENT_STATUS_RETRIEVED_SUCCESS));
    }

    @Operation(summary = "약관 동의 처리", description = "사용자가 특정 약관에 동의합니다.")
    @PostMapping("/agree")
    public ResponseEntity<ApiResponse<Void>> agreeToTerms(
            @Valid @RequestBody UserAgreementRequestDto requestDto) {
        userAgreementService.agreeToTerms(requestDto);
        return ResponseEntity.ok(ApiResponse.successWithOutData(ApiResponseEnum.AGREEMENT_AGREED_SUCCESS));
    }

    @Operation(summary = "약관 재동의", description = "사용자가 약관 변경에 재동의합니다.")
    @PostMapping("/reagree/{userId}/{agreementId}")
    public ResponseEntity<ApiResponse<Void>> reAgreeToTerms(
            @PathVariable UUID userId,
            @PathVariable UUID agreementId) {
        userAgreementService.reAgreeToTerms(userId, agreementId);
        return ResponseEntity.ok(ApiResponse.successWithOutData(ApiResponseEnum.AGREEMENT_REAGREED_SUCCESS));
    }

}
