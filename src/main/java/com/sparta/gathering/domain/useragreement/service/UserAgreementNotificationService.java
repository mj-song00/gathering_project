package com.sparta.gathering.domain.useragreement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.common.service.ReagreeOneTimeTokenService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAgreementNotificationService {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ReagreeOneTimeTokenService reagreeOneTimeTokenService;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendReAgreementEmail(String email, UUID userId, UUID agreementId, String content, String updatedAt) {
        try {
            // 1회성 토큰 생성
            String token = reagreeOneTimeTokenService.generateToken(userId, agreementId);

            // 재동의 URL 생성 (토큰 포함)
            String reAgreeUrl = baseUrl + "/api/user-agreements/reagree?token=" + token;

            // 메시지 Body 구성
            Map<String, String> messageBody = new HashMap<>();
            messageBody.put("email", email);
            messageBody.put("content", content);
            messageBody.put("updatedAt", updatedAt);
            messageBody.put("reAgreeUrl", reAgreeUrl); // URL 포함

            // 메시지 JSON 변환
            String jsonMessage = objectMapper.writeValueAsString(messageBody);

            // SQS 메시지 전송
            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(jsonMessage)
                    .build();

            sqsClient.sendMessage(request);
            log.info("Message sent to SQS queue: {}", queueUrl);

        } catch (Exception e) {
            log.error("Failed to send message to SQS queue", e);
            throw new BaseException(ExceptionEnum.EMAIL_SEND_FAILURE);
        }
    }
}
