package com.sparta.gathering.common.scheduler;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
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

    // @Scheduled(cron = "0 * * * * *")  // 매 1분마다 실행
    @Scheduled(cron = "0 0 12 * * *")  // 매일 12시에 실행
    public void runAgreementExpirationJob() {
        runJobWithUniqueParameters(agreementExpirationJob);
    }

    private void runJobWithUniqueParameters(Job job) {
        try {
            jobLauncher.run(
                    job,
                    new JobParametersBuilder()
                            .addLong("timestamp", System.currentTimeMillis())
                            .toJobParameters()
            );
        } catch (JobExecutionException e) {
            log.error("Failed to execute job: {}", job.getName(), e);
            throw new BaseException(ExceptionEnum.BATCH_JOB_EXEC_FAILED);
        }
    }

}
