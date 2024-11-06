package com.sparta.gathering.domain.chat.controller;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.domain.chat.entity.ChatMessage;
import com.sparta.gathering.domain.chat.service.ChatService;
import com.sparta.gathering.domain.member.service.MemberServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "실시간채팅 API", description = "실시간채팅 API")
public class ChatController {


    private final MemberServiceImpl memberService;
    private final ChatService chatService;
    private final ChannelTopic topic;

    @GetMapping("/api/checkMembership")
    @Operation(summary = "맴버십 확인", description = "맴버십 확인")
    public boolean checkMembership(@RequestParam Long gatheringId,
                                   @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        // 사용자와 모임 ID를 통해 멤버십 확인
        return memberService.isUserInGathering(gatheringId, authenticatedUser);
    }

    @MessageMapping("/chat.sendMessage/{gatheringId}")
    @SendTo("/topic/gathering/{gatheringId}")
    @Operation(summary = "메시지 전송", description = "메시지 전송")
    public ChatMessage sendMessage(@DestinationVariable Long gatheringId, ChatMessage chatMessage) {
        chatMessage.setGatheringId(gatheringId);
        return chatService.saveAndPublishMessage(chatMessage, topic);  // 메시지 저장 및 퍼블리시
    }

    @MessageMapping("/chat.addUser/{gatheringId}")
    @SendTo("/topic/gathering/{gatheringId}")
    @Operation(summary = "입장 메시지 전송", description = "회원 입장 메시지 전송")
    public ChatMessage addUser(ChatMessage chatMessage) {
        chatMessage.setContent(chatMessage.getSender() + "님이 입장하셨습니다.");
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        return chatService.saveAndPublishMessage(chatMessage, topic);  // 입장 메시지 저장 및 퍼블리시
    }

    @MessageMapping("/chat.leaveUser/{gatheringId}")
    @SendTo("/topic/gathering/{gatheringId}")
    @Operation(summary = "퇴실 메시지 전송", description = "회원 퇴실 메시지 전송")
    public ChatMessage leaveUser(ChatMessage chatMessage) {
        chatMessage.setContent(chatMessage.getSender() + "님이 퇴장하셨습니다.");
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        return chatService.saveAndPublishMessage(chatMessage, topic);  // 퇴장 메시지 저장 및 퍼블리시
    }

    @GetMapping("/api/chat/history/{gatheringId}")
    @ResponseBody
    @Operation(summary = "대화 내용 조회", description = "대화 내용 조회")
    public List<ChatMessage> getChatHistory(@PathVariable Long gatheringId) {
        return chatService.getChatHistory(gatheringId);
    }

}
