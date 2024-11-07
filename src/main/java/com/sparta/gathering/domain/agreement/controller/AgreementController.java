package com.sparta.gathering.domain.agreement.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.agreement.dto.request.AgreementRequestDto;
import com.sparta.gathering.domain.agreement.dto.request.AgreementUpdateRequestDto;
import com.sparta.gathering.domain.agreement.entity.Agreement;
import com.sparta.gathering.domain.agreement.service.AgreementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Agreement", description = "약관 API")
@RequestMapping("/api/agreements")
@RequiredArgsConstructor
public class AgreementController {

    private final AgreementService agreementService;

    @Operation(summary = "약관 생성", description = "약관을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createAgreement(
            @Valid @RequestBody AgreementRequestDto agreementRequestDto) {
        agreementService.createAgreement(agreementRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.successWithOutData(ApiResponseEnum.AGREEMENT_CREATED_SUCCESS));
    }

    @Operation(summary = "최신 약관 조회", description = "최신 약관을 조회합니다.")
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<Agreement>> getLatestAgreement() {
        Agreement agreement = agreementService.getLatestAgreementOrThrow();
        ApiResponse<Agreement> response = ApiResponse.successWithData(agreement,
                ApiResponseEnum.AGREEMENT_RETRIEVED_SUCCESS);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "약관 수정", description = "약관을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Agreement>> updateAgreement(
            @PathVariable UUID id,
            @Valid @RequestBody AgreementUpdateRequestDto agreementUpdateRequestDto) {
        Agreement updatedAgreement = agreementService.updateAgreement(id, agreementUpdateRequestDto);
        return ResponseEntity.ok(
                ApiResponse.successWithData(updatedAgreement, ApiResponseEnum.AGREEMENT_UPDATED_SUCCESS));
    }
}
