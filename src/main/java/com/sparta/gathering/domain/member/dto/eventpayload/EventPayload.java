package com.sparta.gathering.domain.member.dto.eventpayload;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EventPayload {
    private String title;
    private String nickname;
    private EventType eventType;
    private Long gatherId;
}