package com.sparta.gathering.domain.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class NotificationRepository {
    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<UUID, List<Object>> messageCache = new ConcurrentHashMap<>();

    public SseEmitter save(UUID userId, SseEmitter sseEmitter) {
        emitters.put(userId, sseEmitter);
        return emitters.get(userId);
    }


    public void deleteById(UUID userId) {
        emitters.remove(userId);
    }

    public SseEmitter findByMemberId(UUID userId) {
        return emitters.get(userId);
    }

    public void addMessageToCache(UUID userId, Object message) {
        messageCache.computeIfAbsent(userId, k -> new ArrayList<>()).add(message);
    }

    public List<Object> getMessagesFromCache(UUID userId) {
        return messageCache.getOrDefault(userId, Collections.emptyList());
    }
}
