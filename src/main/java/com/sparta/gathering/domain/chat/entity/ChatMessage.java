package com.sparta.gathering.domain.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "chat_massage")
public class ChatMessage implements Serializable { // Serializable 구현

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long gatheringId;
    private String content;
    private String sender;
    private MessageType type;
    private LocalDateTime timestamp;

    public enum MessageType {
        JOIN, LEAVE, CHAT
    }


}
