package com.sparta.gathering.domain.chat.controller;

import com.sparta.gathering.domain.chat.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/chat.sendMessage/{gatheringId}")
    @SendTo("/topic/gathering/{gatheringId}")
    public ChatMessage sendMessage(@DestinationVariable Long gatheringId, ChatMessage chatMessage) {
        if (gatheringId == null) {
            gatheringId = 1L;  // 기본값 설정
        }
        chatMessage.setGatheringId(gatheringId);
        return chatMessage;
    }

    @MessageMapping("/chat.addUser/{gatheringId}")
    @SendTo("/topic/gathering/{gatheringId}")
    public ChatMessage addUser(@DestinationVariable Long gatheringId, ChatMessage chatMessage) {
        chatMessage.setContent(chatMessage.getSender() + "님이 입장하셨습니다.");
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        return chatMessage;  // 입장 메시지를 같은 모임의 모든 클라이언트에게 전송
    }

    @MessageMapping("/chat.leaveUser/{gatheringId}")
    @SendTo("/topic/gathering/{gatheringId}")
    public ChatMessage leaveUser(@DestinationVariable Long gatheringId, ChatMessage chatMessage) {
        chatMessage.setContent(chatMessage.getSender() + "님이 퇴장하셨습니다.");
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        return chatMessage;  // 퇴장 메시지를 같은 모임의 모든 클라이언트에게 전송
    }

}
