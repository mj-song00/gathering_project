package com.sparta.gathering.domain.notification.service;

import com.sparta.gathering.domain.member.dto.eventpayload.EventPayload;
import com.sparta.gathering.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final NotificationRepository notificationRepository;

    public SseEmitter subscribe(UUID userId) {

        SseEmitter sseEmitter = notificationRepository.save(userId, new SseEmitter(DEFAULT_TIMEOUT));
        sseEmitter.onCompletion(() -> notificationRepository.deleteById(userId));
        sseEmitter.onTimeout(() -> notificationRepository.deleteById(userId));

        //더미데이터 전송
        sendToClient(userId, "subscribe event, memberId : " + userId);

        List<Object> messages = notificationRepository.getMessagesFromCache(userId);
        messages.forEach(message -> sendToClient(userId, message));


        return sseEmitter;
    }

    @Override
    public void broadcast(UUID userId, EventPayload eventPayload) {
        sendToClient(userId, eventPayload);
        notificationRepository.addMessageToCache(userId, eventPayload);
    }

    private void sendToClient(UUID userId, Object data) {
        SseEmitter sseEmitter = notificationRepository.findByMemberId(userId);
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event().data(data));
            } catch (IOException ex) {
                notificationRepository.deleteById(userId);
                throw new RuntimeException("Connection error", ex);
            }
        } else {
            log.warn("No active connection for user: " + userId);
        }
    }
}
