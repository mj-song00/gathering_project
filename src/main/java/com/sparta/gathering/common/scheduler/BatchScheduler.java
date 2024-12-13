package com.sparta.gathering.common.scheduler;

import com.sparta.gathering.common.service.BatchLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job agreementExpirationJob;
    private final Job userAgreementPendingReagreeJob;
    private final Job redisRankingJob;
    private final BatchLockService batchLockService;

    @Scheduled(cron = "0 0 4 * * *") // 매일 새벽 4시에 실행
    public void runAgreementExpirationJob() {
        runBatchJob("agreementExpirationJob", agreementExpirationJob, 3); // 최대 재시도 3회
    }

    @Scheduled(cron = "0 0 5 * * *") // 매일 새벽 5시에 실행
    public void runUserAgreementPendingReagreeJob() {
        runBatchJob("userAgreementPendingReagreeJob", userAgreementPendingReagreeJob, 3); // 최대 재시도 3회
    }

    @Scheduled(cron = "0 0 0 * * *") //매일 자정 실행
    public void runRedisRankingJob() {
        runBatchJob("redisRankingJob", redisRankingJob,3); // 최대 재시도 3회
    }


    private void runBatchJob(String jobName, Job job, int remainingAttempts) {
        if (remainingAttempts <= 0) {
            log.error("Exceeded maximum retry attempts for job '{}'. Job execution aborted.", jobName);
            return;
        }

        if (batchLockService.acquireLock(jobName)) {
            try {
                log.info("Starting job '{}'", jobName);
                jobLauncher.run(job, createUniqueJobParameters());
                log.info("Job '{}' completed successfully.", jobName);
            } catch (Exception e) {
                log.error("Failed to execute job '{}': {}", jobName, e.getMessage(), e);
            } finally {
                batchLockService.releaseLock(jobName);
                log.info("Lock released for job '{}'.", jobName);
            }
        } else {
            handleRetry(jobName, job, remainingAttempts - 1);
        }
    }

    private void handleRetry(String jobName, Job job, int remainingAttempts) {
        log.info("Retrying to acquire lock for job '{}' (Remaining attempts: {})", jobName, remainingAttempts);
        try {
            Thread.sleep(2000); // 2초 대기 후 재시도
            runBatchJob(jobName, job, remainingAttempts);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Retry interrupted for job '{}'.", jobName);
        }
    }

    private JobParameters createUniqueJobParameters() {
        return new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis()).toJobParameters();
    }

    // 테스트용 배치 실행 메서드
    public boolean runBatchJobForTesting(String jobName, int retries) {
        Job job = resolveJob(jobName);
        if (job == null) {
            log.error("Job '{}' not found for testing.", jobName);
            return false;
        }
        try {
            runBatchJob(jobName, job, retries);
            return true;
        } catch (Exception e) {
            log.error("Job '{}' failed during testing: {}", jobName, e.getMessage());
            return false;
        }
    }

    // Job 이름으로 Job 객체 찾기 (테스트용)
    private Job resolveJob(String jobName) {
        if ("agreementExpirationJob".equals(jobName)) {
            return agreementExpirationJob;
        } else if ("userAgreementPendingReagreeJob".equals(jobName)) {
            return userAgreementPendingReagreeJob;
        }
        return null;
    }

}






