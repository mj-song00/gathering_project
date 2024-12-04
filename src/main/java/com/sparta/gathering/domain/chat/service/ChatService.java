package com.sparta.gathering.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.chat.dto.ChatMessageDto;
import com.sparta.gathering.domain.chat.entity.ChatMessage;
import com.sparta.gathering.domain.chat.enums.MessageType;
import com.sparta.gathering.domain.chat.repository.ChatMessageRepository;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final GatherRepository gatherRepository;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    // 채팅 메시지 조회
    @Transactional(readOnly = true)
    public Page<ChatMessageDto> getChatMessages(Long roomId, int page, int size) {
        if (!gatherRepository.existsById(roomId)) {
            throw new BaseException(ExceptionEnum.ROOM_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return chatMessageRepository.findByRoom_IdOrderByCreatedAtDesc(roomId, pageable)
                .map(this::convertToDto);
    }

    // 채팅 메시지 전송
    @Transactional
    public void sendMessage(ChatMessageDto chatMessageDto, UUID userId, Long roomId) {

        User user = findUserById(userId);
        Gather room = findRoomById(roomId);

        ChatMessage message = new ChatMessage();
        message.setRoom(room);
        message.setSender(user);
        message.setSenderNickname(user.getNickName());
        message.setMessage(chatMessageDto.getMessage());
        message.setCreatedAt(LocalDateTime.now());
        message.setMessageType(MessageType.CHAT);

        chatMessageRepository.save(message);

        ChatMessageDto messageDto = convertToDto(message);
        publishChatMessage(messageDto);
    }

    // 채팅 메시지 전송 (입장)
    @Transactional
    public void sendJoinMessage(UUID userId, Long roomId) {

        User user = findUserById(userId);
        Gather room = findRoomById(roomId);

        ChatMessage message = new ChatMessage();
        message.setRoom(room);
        message.setSender(user);
        message.setMessage(user.getNickName() + "님이 입장하셨습니다.");
        message.setCreatedAt(LocalDateTime.now());
        message.setMessageType(MessageType.JOIN);

        chatMessageRepository.save(message);

        ChatMessageDto messageDto = convertToDto(message);
        publishChatMessage(messageDto);
    }

    // 채팅 메시지 전송 (퇴장)
    @Transactional
    public void sendLeaveMessage(UUID userId, Long roomId) {

        User user = findUserById(userId);
        Gather room = findRoomById(roomId);

        ChatMessage message = new ChatMessage();
        message.setRoom(room);
        message.setSender(user);
        message.setMessage(user.getNickName() + "님이 퇴장하셨습니다.");
        message.setCreatedAt(LocalDateTime.now());
        message.setMessageType(MessageType.LEAVE);

        chatMessageRepository.save(message);

        ChatMessageDto messageDto = convertToDto(message);
        publishChatMessage(messageDto);
    }

    // ChatMessage -> ChatMessageDto 변환
    private ChatMessageDto convertToDto(ChatMessage entity) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(entity.getId());
        dto.setRoomId(entity.getRoom().getId());
        dto.setSenderId(entity.getSender().getId().toString());
        dto.setSenderNickname(entity.getSender().getNickName());
        dto.setMessage(entity.getMessage());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setMessageType(entity.getMessageType());
        return dto;
    }

    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }

    private Gather findRoomById(Long roomId) {
        return gatherRepository.findById(roomId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.ROOM_NOT_FOUND));
    }

    // 채팅 메시지 퍼블리시 (Redis) - 채팅 메시지를 Redis로 퍼블리시하여 WebSocket으로 브로드캐스트
    // 퍼블리시 = 메시지 발행
    // 브로드캐스트 = 한 번에 여러 사람에게 메시지를 전달
    // 브로드 캐스트는 Listener에서 메시지를 받아 WebSocket으로 전달
    public void publishChatMessage(ChatMessageDto chatMessageDto) {

        String topic = "chat_channel";
        try {
            String message = objectMapper.writeValueAsString(chatMessageDto);
            redisTemplate.convertAndSend(topic, message);
        } catch (JsonProcessingException e) {
            log.error("채팅 메시지 퍼블리시 실패", e);
        }
    }

}