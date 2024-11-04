package com.sparta.gathering.domain.chat.controller;

import com.sparta.gathering.common.config.Redis.RedisPublisher;
import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.domain.chat.entity.ChatMessage;
import com.sparta.gathering.domain.chat.service.ChatService;
import com.sparta.gathering.domain.member.service.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ChatController {


    private final MemberServiceImpl memberService;
    private final ChatService chatService;
    private final ChannelTopic topic;

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
        return chatService.saveAndPublishMessage(chatMessage, topic);  // 메시지 저장 및 퍼블리시
    }

    @MessageMapping("/chat.addUser/{gatheringId}")
    @SendTo("/topic/gathering/{gatheringId}")
    public ChatMessage addUser(ChatMessage chatMessage) {
        chatMessage.setContent(chatMessage.getSender() + "님이 입장하셨습니다.");
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        return chatService.saveAndPublishMessage(chatMessage, topic);  // 입장 메시지 저장 및 퍼블리시
    }

    @MessageMapping("/chat.leaveUser/{gatheringId}")
    @SendTo("/topic/gathering/{gatheringId}")
    public ChatMessage leaveUser(ChatMessage chatMessage) {
        chatMessage.setContent(chatMessage.getSender() + "님이 퇴장하셨습니다.");
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        return chatService.saveAndPublishMessage(chatMessage, topic);  // 퇴장 메시지 저장 및 퍼블리시
    }

    @GetMapping("/api/chat/history/{gatheringId}")
    @ResponseBody
    public List<ChatMessage> getChatHistory(@PathVariable Long gatheringId) {
        return chatService.getChatHistory(gatheringId);
    }

}
