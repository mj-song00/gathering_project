package com.sparta.gathering.domain.useragreement.service;

import com.sparta.gathering.common.service.EmailNotificationService;
import jakarta.mail.MessagingException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAgreementNotificationService {

    private final EmailNotificationService emailNotificationService;

    @Value("${agreement.reagree-url}")
    private String reAgreeBaseUrl;

    public void sendReAgreeNotification(String email, String userId, String agreementId) throws MessagingException {
        String reAgreeUrl = String.format("%s/%s/%s", reAgreeBaseUrl, userId, agreementId);
        Map<String, Object> templateModel = Map.of("reagreeUrl", reAgreeUrl);

        emailNotificationService.sendEmailWithTemplate(email, "약관 재동의 요청", "reagree-notification", templateModel);
    }
}
