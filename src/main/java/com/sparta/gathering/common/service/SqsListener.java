package com.sparta.gathering.common.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class SqsListener {

    private final SqsClient sqsClient;
    private final EmailNotificationService emailNotificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    @Scheduled(fixedRate = 5000) // 5초마다 메시지 폴링
    public void pollMessages() {
        try {
            // SQS 메시지 수신
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(10)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

            for (Message message : messages) {
                processMessage(message);
                deleteMessage(message); // 메시지 삭제
            }
        } catch (Exception e) {
            log.error("SQS 대기열에서 메시지를 폴링하는 중 오류가 발생했습니다.", e);
        }
    }

    private void processMessage(Message message) {
        try {
            // 메시지 Body를 JSON으로 파싱
            Map<String, Object> body = objectMapper.readValue(message.body(), new TypeReference<>() {
            });

            String email = (String) body.get("email");
            String content = (String) body.get("content");
            String updatedAt = (String) body.get("updatedAt");
            String reAgreeUrl = (String) body.get("reAgreeUrl"); // URL 포함

            // 템플릿 데이터 설정
            Map<String, Object> templateModel = Map.of(
                    "content", content,
                    "updatedAt", updatedAt,
                    "reagreeUrl", reAgreeUrl
            );

            // 이메일 전송
            emailNotificationService.sendEmailWithTemplate(
                    email,
                    "약관 재동의 요청",
                    "reagree-notification",
                    templateModel
            );

            log.info("이메일 전송 대상 {}", email);
        } catch (Exception e) {
            log.error("메시지를 처리하지 못했습니다.: {}", message.body(), e);
        }
    }

    private void deleteMessage(Message message) {
        // 메시지 삭제
        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build();

        sqsClient.deleteMessage(deleteRequest);
        log.info("대기열에서 메시지가 삭제되었습니다.: {}", message.messageId());
    }
}