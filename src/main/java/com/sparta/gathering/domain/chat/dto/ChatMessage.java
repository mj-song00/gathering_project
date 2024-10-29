package com.sparta.gathering.domain.chat.dto;

import lombok.*;

@Getter
@Setter
public class ChatMessage {
    private String gatheringId;
    private String sender;
    private String content;
    private MessageType type;


    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

}




