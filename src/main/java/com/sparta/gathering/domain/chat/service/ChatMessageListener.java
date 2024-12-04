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

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            ChatMessageDto chatMessage = objectMapper.readValue(message.getBody(), ChatMessageDto.class);

            // WebSocket으로 브로드캐스트
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + chatMessage.getRoomId(),
                    chatMessage
            );

        } catch (IOException e) {
            log.error("Redis 채팅 처리 실패", e);
        }
    }

}