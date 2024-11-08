package com.sparta.gathering.domain.emailverification.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.common.service.EmailNotificationService;
import com.sparta.gathering.common.service.RedisVerificationService;
import jakarta.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedisVerificationService redisVerificationService;
    private final EmailNotificationService emailNotificationService;

    // 인증 코드 전송
    public void sendVerificationCode(String email) throws MessagingException {
        String code = generateVerificationCode();
        redisVerificationService.saveVerificationCode(email, code);

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("code", code);

        emailNotificationService.sendEmailWithTemplate(
                email,
                "이메일 인증 코드",
                "verification-email",
                templateModel
        );
    }

    // 인증 코드 확인
    public void confirmVerificationCode(String email, String code) {
        boolean isValid = redisVerificationService.verifyCode(email, code);
        if (!isValid) {
            throw new BaseException(ExceptionEnum.INVALID_VERIFICATION_CODE);  // 인증 실패 시 예외 발생
        }
        redisVerificationService.deleteCode(email);  // 인증 성공 시 Redis에서 코드 삭제
        redisVerificationService.saveVerifiedStatus(email); // 인증 완료 상태 저장
    }

    // 6자리의 랜덤 숫자로 구성된 인증 코드 생성
    private String generateVerificationCode() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
    }

    // 인증 완료 상태 확인
    public boolean isVerified(String email) {
        return redisVerificationService.isCodeVerified(email);
    }

}