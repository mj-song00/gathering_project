package com.sparta.gathering.common.config.redis;

import com.sparta.gathering.domain.chat.service.ChatMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class ChatRedisConfig {

    // 채팅 메시지 발행/구독을 위한 토픽
    @Bean
    public ChannelTopic chatTopic() {
        return new ChannelTopic("chat_channel");
    }

    // Redis 메시지 리스너 컨테이너 설정
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter messageListenerAdapter
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListenerAdapter, chatTopic());
        return container;
    }

    // 메시지 리스너 어댑터 설정
    @Bean
    public MessageListenerAdapter messageListenerAdapter(ChatMessageListener redisMessageSubscriber) {
        return new MessageListenerAdapter(redisMessageSubscriber, "onMessage");
    }

}