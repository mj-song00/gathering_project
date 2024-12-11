package com.sparta.gathering.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.gathering.domain.chat.dto.ChatMessageDto;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Redis에서 메시지를 수신했을 때 호출되는 메서드
     *
     * @param message 수신된 메시지
     * @param pattern 패턴 (Redis에서 발행된 메시지를 수신하는 경우 패턴이 사용되지 않아서 사용 안함)
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // Redis 메시지를 ChatMessageDto 객체로 변환
            ChatMessageDto chatMessage = objectMapper.readValue(message.getBody(), ChatMessageDto.class);

            // WebSocket으로 브로드캐스트
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + chatMessage.getRoomId(), // 대상 채팅방
                    chatMessage // 전송할 메시지
            );

        } catch (IOException e) {
            // 메시지 처리 중 예외 발생 시 로그 출력
            log.error("Redis 채팅 처리 실패", e);
        }
    }

}