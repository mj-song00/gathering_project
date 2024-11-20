package com.sparta.gathering.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SlackNotifierService {

    // WebClient 와 웹훅 URL 을 저장하는 변수
    private final WebClient webClient;
    private final String webhookUrl;

    // 생성자에서 웹훅 URL 을 주입받고 WebClient 초기화
    public SlackNotifierService(@Value("${slack.webhook.url}") String webhookUrl) {
        this.webClient = WebClient.builder().build();
        this.webhookUrl = webhookUrl;
    }

    // Slack 알림을 보내는 메서드
    public void sendNotification(String message) {
        // 요청 페이로드를 생성
        Map<String, String> payload = new HashMap<>();
        payload.put("text", message);

        // WebClient 를 사용해 POST 요청 전송
        webClient.post()
                .uri(webhookUrl) // 웹훅 URL
                .body(Mono.just(payload), Map.class) // 본문에 페이로드 추가
                .retrieve() // 응답 수신
                .bodyToMono(String.class) // 응답을 문자열로 변환
                .doOnError(e -> log.error("Slack 알림 전송 실패", e)) // 오류 발생 시 로그 기록
                .subscribe(response -> log.info("Slack 알림 전송 성공: {}", response)); // 성공 시 로그 기록
    }
}
