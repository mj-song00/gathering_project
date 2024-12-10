package com.sparta.gathering.domain.notification.service;

import com.sparta.gathering.domain.member.dto.eventpayload.EventPayload;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

public interface NotificationService {


    SseEmitter subscribe(UUID userId);

    void broadcast(UUID userId, EventPayload eventPayload);
}
