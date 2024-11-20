package com.sparta.gathering.common.controller;

import com.sparta.gathering.common.scheduler.BatchScheduler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@Tag(name = "Batch Test", description = "배치 테스트 API / 이정현")
@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchTestController {

    private final BatchScheduler batchScheduler;

    @Operation(summary = "약관 만료 배치 실행", description = "약관 만료 배치를 실행합니다, 테스트용도로 dev profile에서만 사용합니다.")
    @PostMapping("/runAgreementExpiration")
    public ResponseEntity<String> runAgreementExpirationJob() {
        batchScheduler.runAgreementExpirationJob();
        return ResponseEntity.ok("Agreement expiration job executed.");
    }

}
