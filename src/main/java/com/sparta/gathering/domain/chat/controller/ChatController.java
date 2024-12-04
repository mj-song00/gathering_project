package com.sparta.gathering.domain.chat.controller;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.domain.chat.dto.ChatMessageDto;
import com.sparta.gathering.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "채팅 API", description = "채팅 관련 API / 이정현")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "채팅방 메시지 조회", description = "채팅방의 메시지를 조회합니다, 페이지네이션을 지원하며 기본값은 20입니다.")
    @GetMapping("/api/chat/messages/{roomId}")
    public Page<ChatMessageDto> getChatMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return chatService.getChatMessages(roomId, page, size);
    }

    @Operation(summary = "채팅방 메시지 전송", description = "채팅방에 메시지를 전송합니다.")
    @PostMapping("/api/chat/send/{roomId}")
    public void sendMessage(
            @RequestBody ChatMessageDto messageDto,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long roomId) {
        chatService.sendMessage(messageDto, authenticatedUser.getUserId(), roomId);
    }

    @Operation(summary = "채팅방 참여메세지 전송", description = "채팅방에 참여 메세지를 전송합니다.")
    @PostMapping("/api/chat/join/{roomId}")
    public void joinRoom(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long roomId) {
        chatService.sendJoinMessage(authenticatedUser.getUserId(), roomId);
    }

    @Operation(summary = "채팅방 나가기메세지 전송", description = "채팅방에 나가기 메세지를 전송합니다.")
    @PostMapping("/api/chat/leave/{roomId}")
    public void leaveRoom(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long roomId) {
        chatService.sendLeaveMessage(authenticatedUser.getUserId(), roomId);
    }

}