package com.sparta.gathering.domain.chat.controller;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.domain.chat.dto.ChatMessage;
import com.sparta.gathering.domain.member.service.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatController {

    private final MemberServiceImpl memberService;

    @GetMapping("/api/checkMembership")
    public boolean checkMembership(@RequestParam Long gatheringId,
                                   @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        // 사용자와 모임 ID를 통해 멤버십 확인
        return memberService.isUserInGathering(gatheringId, authenticatedUser);
    }

    @MessageMapping("/chat.sendMessage/{gatheringId}")
    @SendTo("/topic/gathering/{gatheringId}")
    public ChatMessage sendMessage(@DestinationVariable Long gatheringId, ChatMessage chatMessage) {
        chatMessage.setGatheringId(gatheringId);
        return chatMessage;
    }

    @MessageMapping("/chat.addUser/{gatheringId}")
    @SendTo("/topic/gathering/{gatheringId}")
    public ChatMessage addUser(ChatMessage chatMessage) {
        chatMessage.setContent(chatMessage.getSender() + "님이 입장하셨습니다.");
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        return chatMessage;  // 입장 메시지를 같은 모임의 모든 클라이언트에게 전송
    }

    @MessageMapping("/chat.leaveUser/{gatheringId}")
    @SendTo("/topic/gathering/{gatheringId}")
    public ChatMessage leaveUser(ChatMessage chatMessage) {
        chatMessage.setContent(chatMessage.getSender() + "님이 퇴장하셨습니다.");
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        return chatMessage;  // 퇴장 메시지를 같은 모임의 모든 클라이언트에게 전송
    }
}
