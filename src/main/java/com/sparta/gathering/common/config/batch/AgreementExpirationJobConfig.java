package com.sparta.gathering.common.config.batch;

import com.sparta.gathering.domain.useragreement.entity.UserAgreement;
import com.sparta.gathering.domain.useragreement.repository.UserAgreementRepository;
import com.sparta.gathering.domain.useragreement.service.UserAgreementService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class AgreementExpirationJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final UserAgreementRepository userAgreementRepository;
    private final UserAgreementService userAgreementService;

    @Bean
    public Job agreementExpirationJob() {
        return new JobBuilder("agreementExpirationJob", jobRepository)
                .start(expireAgreementsStep())
                .build();
    }

    @Bean
    public Step expireAgreementsStep() {
        return new StepBuilder("expireAgreementsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    LocalDateTime expirationDateTime = LocalDateTime.now().minusDays(30);

                    // 만료 대상 약관을 조회하여 히스토리에 기록
                    List<UserAgreement> agreementsToExpire = userAgreementRepository.findPendingReAgreementsOlderThan(
                            expirationDateTime);

                    agreementsToExpire.forEach(userAgreementService::saveUserAgreementHistory);

                    // 상태를 EXPIRED로 일괄 업데이트
                    int updatedCount = userAgreementRepository.expireOldPendingReAgreements(expirationDateTime);
                    log.info("Number of agreements updated to EXPIRED: {}", updatedCount);

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
