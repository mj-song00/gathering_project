package com.sparta.gathering.domain.emailverification.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.emailverification.dto.request.EmailVerificationRequestDto;
import com.sparta.gathering.domain.emailverification.dto.request.VerificationCodeRequestDto;
import com.sparta.gathering.domain.emailverification.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "emailVerification", description = "이메일 인증 API / 이정현")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/verification")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @Operation(summary = "이메일 인증 코드 발송", description = "이메일로 인증 코드를 발송합니다.")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(
            @Valid @RequestBody EmailVerificationRequestDto requestDto) {
        emailVerificationService.sendVerificationCode(requestDto.getEmail());
        return ResponseEntity.ok(ApiResponse.successWithOutData(ApiResponseEnum.EMAIL_VERIFICATION_CODE_SENT));
    }

    @Operation(summary = "인증 코드 확인", description = "사용자가 입력한 인증 코드를 검증합니다.")
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmVerificationCode(
            @Valid @RequestBody VerificationCodeRequestDto requestDto) {
        emailVerificationService.confirmVerificationCode(requestDto.getEmail(), requestDto.getCode());
        return ResponseEntity.ok(ApiResponse.successWithOutData(ApiResponseEnum.EMAIL_VERIFICATION_SUCCESS));
    }

}
