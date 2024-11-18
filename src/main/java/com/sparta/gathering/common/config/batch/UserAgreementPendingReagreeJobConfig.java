package com.sparta.gathering.common.config.batch;

import com.sparta.gathering.domain.agreement.entity.Agreement;
import com.sparta.gathering.domain.agreement.enums.AgreementStatus;
import com.sparta.gathering.domain.useragreement.entity.UserAgreement;
import com.sparta.gathering.domain.useragreement.repository.UserAgreementRepository;
import com.sparta.gathering.domain.useragreement.service.UserAgreementNotificationService;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class UserAgreementPendingReagreeJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final UserAgreementRepository userAgreementRepository;
    private final UserAgreementNotificationService notificationService;

    @Bean
    public Job userAgreementPendingReagreeJob() {
        return new JobBuilder("userAgreementPendingReagreeJob", jobRepository)
                .start(updateUserAgreementsStep())
                .build();
    }

    @Bean
    public Step updateUserAgreementsStep() {
        return new StepBuilder("updateUserAgreementsStep", jobRepository)
                .<UserAgreement, UserAgreement>chunk(500, transactionManager)
                .reader(userAgreementReader(null))
                .processor(userAgreementProcessor())
                .writer(userAgreementWriter())
                .build();
    }

    @Bean
    @StepScope
    public UserAgreementReader userAgreementReader(
            @Value("#{jobParameters['agreementId']}") String agreementIdStr) {
        return new UserAgreementReader(userAgreementRepository, agreementIdStr);
    }

    @Bean
    public ItemProcessor<UserAgreement, UserAgreement> userAgreementProcessor() {
        return userAgreement -> {
            // 상태를 PENDING_REAGREE로 업데이트
            userAgreement.updateStatus(AgreementStatus.PENDING_REAGREE);

            // 약관 정보 가져오기
            Agreement agreement = userAgreement.getAgreement();
            String agreementContent = agreement.getContent();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String updatedAt = agreement.getUpdatedAt().format(formatter);

            // SQS 큐로 전송
            notificationService.sendReAgreementEmail(
                    userAgreement.getUser().getEmail(),
                    userAgreement.getUser().getId(),
                    agreement.getId(),
                    agreementContent,
                    updatedAt
            );

            return userAgreement;
        };
    }

    @Bean
    public ItemWriter<UserAgreement> userAgreementWriter() {
        return userAgreementRepository::saveAll;
    }
}
