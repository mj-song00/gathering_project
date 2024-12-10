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

//    public void sendEventToClient(UUID userId, Object data) {
//        SseEmitter sseEmitter = findByMemberId(userId);
//        if (sseEmitter != null) {
//            try {
//                sseEmitter.send(SseEmitter.event()
//                        .id(userId.toString())
//                        .name("sse")
//                        .data(data)
//                );
//            } catch (IOException ex) {
//                deleteById(userId);
//                throw new RuntimeException("연결 오류", ex);
//            }
//        } else {
//            // 구독하지 않은 경우 로그 출력
//            System.out.println("No active connection for user: " + userId);
//        }
//    }
}
