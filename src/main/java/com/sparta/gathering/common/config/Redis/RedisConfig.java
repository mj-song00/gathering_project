package com.sparta.gathering.common.config.Redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    private static final String TOPIC_NAME = "chatroom";


    // ChannelTopic Bean 설정 (채팅 주제명 설정)
    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic(TOPIC_NAME);
    }

    // RedisMessageListenerContainer Bean 설정 (Redis 구독 설정)
    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                        MessageListenerAdapter listenerAdapter,
                                                        ChannelTopic topic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, topic); // 구독할 주제 설정
        return container;
    }

    // MessageListenerAdapter Bean 설정 (RedisSubscriber와 연결)
    @Bean
    public MessageListenerAdapter listenerAdapter(WebSocketRedisSubscriber webSocketRedisSubscriber) {
        return new MessageListenerAdapter(webSocketRedisSubscriber, "handleMessage"); // RedisSubscriber의 handleMessage 메서드 호출
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

}