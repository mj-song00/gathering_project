package com.sparta.gathering.common.config.Redis;

import com.sparta.gathering.domain.chat.entity.ChatMessage;
import com.sparta.gathering.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatMessageRepository chatMessageRepository;

    public void publish(ChannelTopic topic, ChatMessage message) {
        chatMessageRepository.save(message);  // 메시지를 DB에 저장
        redisTemplate.convertAndSend(topic.getTopic(), message);  // Redis Pub/Sub 주제로 메시지 발행
    }

}
