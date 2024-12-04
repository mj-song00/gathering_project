package com.sparta.gathering.domain.chat.dto;

import com.sparta.gathering.domain.chat.enums.MessageType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    private Long id;
    private Long roomId;
    private String senderId;
    private String senderNickname;
    private String message;
    private LocalDateTime createdAt;
    private MessageType messageType;

}