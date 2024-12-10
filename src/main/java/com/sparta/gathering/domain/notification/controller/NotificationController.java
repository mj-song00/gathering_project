package com.sparta.gathering.domain.notification.controller;

import com.sparta.gathering.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/noti")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value="/subscribe/{userId}", produces="text/event-stream")
    public SseEmitter subscribe(@PathVariable UUID userId){
        return notificationService.subscribe(userId);
    }

}
