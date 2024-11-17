package com.sparta.gathering.domain.emailverification.service;

import com.sparta.gathering.common.config.redis.EmailVerificationConfig;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.common.service.EmailNotificationService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationConfig emailVerificationConfig;
    private final EmailNotificationService emailNotificationService;

    private static final String TEMPLATE_NAME = "verification-email";
    private static final String SUBJECT = "이메일 인증 코드";
    private static final int MAX_DAILY_SEND_ATTEMPTS = 5;     // 하루 최대 발송 요청 횟수
    private static final int MAX_DAILY_VERIFY_ATTEMPTS = 10;  // 하루 최대 확인 시도 횟수

    // 인증 코드 전송
    @Async
    public void sendVerificationCode(String email) {
        checkDailySendLimit(email);

        String code = generateVerificationCode();
        emailVerificationConfig.saveVerificationCode(email, code);
        incrementDailySendAttempts(email);

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("code", code);

        emailNotificationService.sendEmailWithTemplate(
                email,
                SUBJECT,
                TEMPLATE_NAME,
                templateModel
        );
    }

    // 인증 코드 확인
    public void confirmVerificationCode(String email, String code) {
        checkDailyVerifyLimit(email);

        boolean isValid = emailVerificationConfig.verifyCode(email, code);
        if (!isValid) {
            incrementDailyVerifyAttempts(email);
            throw new BaseException(ExceptionEnum.INVALID_VERIFICATION_CODE);
        }

        emailVerificationConfig.deleteCode(email);  // 인증 성공 시 Redis에서 코드 삭제
        emailVerificationConfig.saveVerifiedStatus(email); // 인증 완료 상태 저장
        // 성공 시 모든 카운터 초기화
        resetDailyAttempts(email);
    }

    // 6자리의 랜덤 숫자로 구성된 인증 코드 생성
    private String generateVerificationCode() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
    }

    // 인증 완료 상태 확인
    public boolean isVerified(String email) {
        return emailVerificationConfig.isCodeVerified(email);
    }

    // 일일 발송 요청 횟수 확인
    private void checkDailySendLimit(String email) {
        int attempts = emailVerificationConfig.getDailySendAttempts(email);
        if (attempts >= MAX_DAILY_SEND_ATTEMPTS) {
            throw new BaseException(ExceptionEnum.DAILY_SEND_LIMIT_EXCEEDED);
        }
    }

    // 일일 확인 시도 횟수 확인
    private void checkDailyVerifyLimit(String email) {
        int attempts = emailVerificationConfig.getDailyVerifyAttempts(email);
        if (attempts >= MAX_DAILY_VERIFY_ATTEMPTS) {
            throw new BaseException(ExceptionEnum.DAILY_VERIFY_LIMIT_EXCEEDED);
        }
    }

    // 일일 발송 요청 횟수 증가
    private void incrementDailySendAttempts(String email) {
        emailVerificationConfig.incrementDailySendAttempts(email);
    }

    // 일일 확인 시도 횟수 증가
    private void incrementDailyVerifyAttempts(String email) {
        emailVerificationConfig.incrementDailyVerifyAttempts(email);
    }

    // 모든 일일 카운터 초기화
    private void resetDailyAttempts(String email) {
        emailVerificationConfig.resetDailySendAttempts(email);
        emailVerificationConfig.resetDailyVerifyAttempts(email);
    }
}