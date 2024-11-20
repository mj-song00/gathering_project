package com.sparta.gathering.common.controller;

import com.sparta.gathering.common.entity.BatchJobRequest;
import com.sparta.gathering.common.scheduler.BatchScheduler;
import com.sparta.gathering.common.service.BatchJobRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@Tag(name = "Batch Test", description = "배치 테스트 API")
@RestController
@RequestMapping("/api/batch/test")
@RequiredArgsConstructor
public class BatchTestController {

    private final BatchScheduler batchScheduler;
    private final BatchJobRequestService batchJobRequestService;

    @Operation(summary = "배치 작업 실행", description = "특정 배치 작업을 실행합니다.")
    @PostMapping("/jobs/{jobName}")
    public ResponseEntity<Map<String, String>> runBatchJob(
            @PathVariable String jobName,
            @RequestParam(value = "retries", defaultValue = "3") int retries) {
        boolean success = batchScheduler.runBatchJobForTesting(jobName, retries);
        return success
                ? ResponseEntity.ok(Map.of("message", "Job executed successfully.", "jobName", jobName))
                : ResponseEntity.status(500).body(Map.of("message", "Job execution failed.", "jobName", jobName));
    }

    @Operation(summary = "대기 중인 배치 작업 요청 조회", description = "특정 배치 작업의 대기 중인 요청을 조회합니다.")
    @GetMapping("/requests")
    public ResponseEntity<List<BatchJobRequest>> getPendingRequests(
            @RequestParam String jobName) {
        List<BatchJobRequest> pendingRequests = batchJobRequestService.getPendingRequests(jobName);
        return ResponseEntity.ok(pendingRequests);
    }

    @Operation(summary = "특정 배치 작업 요청 삭제", description = "특정 ID의 배치 작업 요청을 삭제합니다.")
    @DeleteMapping("/requests/{requestId}")
    public ResponseEntity<Map<String, String>> deleteBatchRequest(@PathVariable Long requestId) {
        batchJobRequestService.deleteRequest(requestId);
        return ResponseEntity.ok(
                Map.of("message", "Batch request deleted successfully.", "requestId", String.valueOf(requestId)));
    }

}
