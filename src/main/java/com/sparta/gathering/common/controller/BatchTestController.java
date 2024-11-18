package com.sparta.gathering.common.controller;

import com.sparta.gathering.common.scheduler.BatchScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchTestController {

    private final BatchScheduler batchScheduler;

    @PostMapping("/runAgreementExpiration")
    public ResponseEntity<String> runAgreementExpirationJob() {
        batchScheduler.runAgreementExpirationJob();
        return ResponseEntity.ok("Agreement expiration job executed.");
    }

}
