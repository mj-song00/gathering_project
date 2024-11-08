package com.sparta.gathering.domain.useragreement.scheduler;

import com.sparta.gathering.domain.agreement.enums.AgreementStatus;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.useragreement.entity.UserAgreement;
import com.sparta.gathering.domain.useragreement.repository.UserAgreementRepository;
import com.sparta.gathering.domain.useragreement.service.UserAgreementNotificationService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserAgreementScheduler {

    private final UserAgreementRepository userAgreementRepository;
    private final UserAgreementNotificationService userAgreementNotificationService;

    @Value("${agreement.reagree-grace-period-days}") // 유예 기간
    private int reAgreeGracePeriodDays;

    // 유예 기간 만료 사용자 검사 (매일 자정에 실행)
    @Scheduled(cron = "0 0 0 * * *")
    public void checkAndExpireAgreements() {
        LocalDateTime expiryDate = LocalDateTime.now().minusDays(reAgreeGracePeriodDays);

        // 만료 대상 조회
        List<UserAgreement> expiredAgreements = userAgreementRepository.findByStatusAndAgreedAtBefore(
                AgreementStatus.PENDING_REAGREE, expiryDate);

        expiredAgreements.forEach(userAgreement -> {
            // 상태를 EXPIRED로 업데이트하고 소프트 삭제 처리
            userAgreement.updateStatus(AgreementStatus.EXPIRED);
            userAgreementRepository.save(userAgreement);

            User user = userAgreement.getUser();
            user.setDeletedAt(); // 사용자 소프트 삭제
            log.info("유예 기간 만료로 소프트 삭제된 사용자 ID: {}", user.getId());
        });

        log.info("유예 기간 만료 검사 및 탈퇴 처리 완료: {}건", expiredAgreements.size());
    }

    // 동의 알림 메일 발송
    @Scheduled(cron = "0 10 0 * * *")
    public void notifyPendingReAgreements() {
        // 알림 전송 기준일 설정
        LocalDateTime notifyBeforeDate = LocalDateTime.now().minusDays((long) reAgreeGracePeriodDays - 25);

        // 재동의 요청 대상 조회
        List<UserAgreement> pendingAgreements = userAgreementRepository.findByStatusAndAgreedAtBefore(
                AgreementStatus.PENDING_REAGREE, notifyBeforeDate);

        pendingAgreements.forEach(userAgreement -> {
            try {
                userAgreementNotificationService.sendReAgreeNotification(
                        userAgreement.getUser().getEmail(),
                        userAgreement.getUser().getId().toString(),
                        userAgreement.getAgreement().getId().toString()
                );
                log.info("재동의 메일 알림 전송 완료: 사용자 ID {}", userAgreement.getUser().getId());
            } catch (Exception e) {
                log.error("재동의 메일 발송 실패: 사용자 ID {}", userAgreement.getUser().getId(), e);
            }
        });
    }
}
