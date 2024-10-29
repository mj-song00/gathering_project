package com.sparta.gathering.domain.chat.controller;

import com.sparta.gathering.domain.chat.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage chatMessage) {
        messagingTemplate.convertAndSend("/topic/gathering/" + chatMessage.getGatheringId(), chatMessage);
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/gathering/{gatheringId}")
    public ChatMessage addUser(ChatMessage chatMessage) {
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        return chatMessage;
    }
}

