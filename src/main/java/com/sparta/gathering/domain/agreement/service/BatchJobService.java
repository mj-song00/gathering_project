package com.sparta.gathering.domain.agreement.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchJobService {

    private final JobLauncher jobLauncher;
    private final Job userAgreementPendingReagreeJob;

    @Async("taskExecutor")
    public void runBatchJob(String agreementId) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("agreementId", agreementId)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(userAgreementPendingReagreeJob, jobParameters);
        } catch (Exception e) {
            log.error("Batch job failed with exception", e);
            throw new BaseException(ExceptionEnum.BATCH_JOB_EXEC_FAILED);
        }
    }

}

