package com.sparta.gathering.common.config.Redis;


import com.sparta.gathering.domain.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisSubscriber {

    private final SimpMessagingTemplate messagingTemplate;

    // Redis로부터 메시지를 수신하여 WebSocket 클라이언트로 전송
    public void handleMessage(ChatMessage message) {
        messagingTemplate.convertAndSend("/topic/gathering/" + message.getGatheringId(), message);
    }

}