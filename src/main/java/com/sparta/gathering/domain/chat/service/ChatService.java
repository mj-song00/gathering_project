package com.sparta.gathering.domain.chat.service;

import com.sparta.gathering.common.config.redis.WebSocketRedisPublisher;
import com.sparta.gathering.domain.chat.entity.ChatMessage;
import com.sparta.gathering.domain.chat.repository.ChatMessageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final WebSocketRedisPublisher webSocketRedisPublisher;

    public ChatMessage saveAndPublishMessage(ChatMessage message, ChannelTopic topic) {
        chatMessageRepository.save(message);  // 메시지를 DB에 저장
        webSocketRedisPublisher.publish(topic, message);  // 메시지를 Redis에 퍼블리시
        return message;
    }

    public List<ChatMessage> getChatHistory(Long gatheringId) {
        return chatMessageRepository.findByGatherIdOrderByTimestamp(gatheringId);
    }

}
