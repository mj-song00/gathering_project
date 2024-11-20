package com.sparta.gathering.common.config.redis;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import java.io.IOException;

@Slf4j
@Configuration
public class EmbeddedRedisConfig {

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.maxmemory}")
    private int maxmemorySize;

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
        redisServer = RedisServer.builder().port(port).setting("maxmemory " + maxmemorySize + "M").build();
        try {
            redisServer.start();
            log.info("레디스 서버 시작 성공");
        } catch (Exception e) {
            log.error("레디스 서버 시작 실패");
        }
    }

    @PreDestroy
    public void stopRedis() {
        this.redisServer.stop();
    }
}