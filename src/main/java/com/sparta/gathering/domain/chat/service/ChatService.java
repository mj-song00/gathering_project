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

    private final ObjectMapper objectMapper; // JSON 변환기
    private final RedisTemplate<String, String> redisTemplate; // Redis 템플릿

    /**
     * 특정 방의 채팅 메시지를 페이지네이션하여 조회합니다.
     *
     * @param roomId 조회할 채팅방의 ID
     * @param page   조회할 페이지 번호
     * @param size   페이지당 메시지 수
     * @return 채팅 메시지의 페이지
     * @throws BaseException 방이 존재하지 않을 경우 예외 발생
     */
    @Transactional(readOnly = true)
    public Page<ChatMessageDto> getChatMessages(Long roomId, int page, int size) {
        if (!gatherRepository.existsById(roomId)) {
            throw new BaseException(ExceptionEnum.ROOM_NOT_FOUND); // 방이 존재하지 않으면 예외 발생
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")); // 페이지 요청 생성
        return chatMessageRepository.findByRoom_IdOrderByCreatedAtDesc(roomId, pageable)
                .map(this::convertToDto); // 조회 결과를 DTO로 변환
    }

    /**
     * 채팅 메시지를 전송합니다.
     *
     * @param chatMessageDto 메시지 DTO
     * @param userId         발신자 ID
     * @param roomId         방 ID
     */
    @Transactional
    public void sendMessage(ChatMessageDto chatMessageDto, UUID userId, Long roomId) {

        User user = findUserById(userId); // 사용자 조회
        Gather room = findRoomById(roomId); // 방 조회

        ChatMessage message = new ChatMessage();
        message.setRoom(room); // 방 설정
        message.setSender(user); // 발신자 설정
        message.setSenderNickname(user.getNickName()); // 발신자 닉네임 설정
        message.setMessage(chatMessageDto.getMessage()); // 메시지 내용 설정
        message.setCreatedAt(LocalDateTime.now()); // 생성 시간 설정
        message.setMessageType(MessageType.CHAT); // 메시지 타입 설정

        chatMessageRepository.save(message); // 메시지 저장

        ChatMessageDto messageDto = convertToDto(message); // 메시지를 DTO로 변환
        publishChatMessage(messageDto); // 메시지 퍼블리시
    }

    /**
     * 채팅방에 참여 메시지를 전송합니다.
     *
     * @param userId 발신자 ID
     * @param roomId 방 ID
     */
    @Transactional
    public void sendJoinMessage(UUID userId, Long roomId) {

        User user = findUserById(userId); // 사용자 조회
        Gather room = findRoomById(roomId); // 방 조회

        ChatMessage message = new ChatMessage();
        message.setRoom(room); // 방 설정
        message.setSender(user); // 발신자 설정
        message.setMessage(user.getNickName() + "님이 입장하셨습니다."); // 입장 메시지 설정
        message.setCreatedAt(LocalDateTime.now()); // 생성 시간 설정
        message.setMessageType(MessageType.JOIN); // 메시지 타입 설정

        chatMessageRepository.save(message); // 메시지 저장

        ChatMessageDto messageDto = convertToDto(message); // 메시지를 DTO로 변환
        publishChatMessage(messageDto); // 메시지 퍼블리시
    }

    /**
     * 채팅방에 퇴장 메시지를 전송합니다.
     *
     * @param userId 발신자 ID
     * @param roomId 방 ID
     */
    @Transactional
    public void sendLeaveMessage(UUID userId, Long roomId) {

        User user = findUserById(userId); // 사용자 조회
        Gather room = findRoomById(roomId); // 방 조회

        ChatMessage message = new ChatMessage();
        message.setRoom(room); // 방 설정
        message.setSender(user); // 발신자 설정
        message.setMessage(user.getNickName() + "님이 퇴장하셨습니다."); // 퇴장 메시지 설정
        message.setCreatedAt(LocalDateTime.now()); // 생성 시간 설정
        message.setMessageType(MessageType.LEAVE); // 메시지 타입 설정

        chatMessageRepository.save(message); // 메시지 저장

        ChatMessageDto messageDto = convertToDto(message); // 메시지를 DTO로 변환
        publishChatMessage(messageDto); // 메시지 퍼블리시
    }

    /**
     * ChatMessage 엔티티를 ChatMessageDto로 변환합니다.
     *
     * @param entity 변환할 ChatMessage 엔티티
     * @return 변환된 ChatMessageDto
     */
    private ChatMessageDto convertToDto(ChatMessage entity) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(entity.getId()); // ID 설정
        dto.setRoomId(entity.getRoom().getId()); // 방 ID 설정
        dto.setSenderId(entity.getSender().getId().toString()); // 발신자 ID 설정
        dto.setSenderNickname(entity.getSender().getNickName()); // 발신자 닉네임 설정
        dto.setMessage(entity.getMessage()); // 메시지 내용 설정
        dto.setCreatedAt(entity.getCreatedAt()); // 생성 시간 설정
        dto.setMessageType(entity.getMessageType()); // 메시지 타입 설정
        return dto;
    }

    /**
     * 사용자 ID로 사용자를 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 조회된 사용자
     * @throws BaseException 사용자가 존재하지 않을 경우 예외 발생
     */
    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND)); // 사용자 조회 실패 시 예외 발생
    }

    /**
     * 방 ID로 방을 조회합니다.
     *
     * @param roomId 조회할 방 ID
     * @return 조회된 방
     * @throws BaseException 방이 존재하지 않을 경우 예외 발생
     */
    private Gather findRoomById(Long roomId) {
        return gatherRepository.findById(roomId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.ROOM_NOT_FOUND)); // 방 조회 실패 시 예외 발생
    }

    /**
     * 채팅 메시지를 퍼블리시 하여 WebSocket으로 브로드캐스트합니다.<br> 퍼블리시 = 메시지를 발행하여 Redis로 전달<br> 브로드캐스트 = 한 번에 여러 사람에게 메시지를 전달<br> 브로드
     * 캐스트는 Listener에서 메시지를 받아 WebSocket으로 전달
     *
     * @param chatMessageDto 퍼블리시할 채팅 메시지 DTO
     */
    public void publishChatMessage(ChatMessageDto chatMessageDto) {
        String topic = "chat_channel";
        try {
            String message = objectMapper.writeValueAsString(chatMessageDto); // 메시지를 JSON 문자열로 변환
            redisTemplate.convertAndSend(topic, message); // Redis로 메시지 퍼블리시
        } catch (JsonProcessingException e) {
            log.error("채팅 메시지 퍼블리시 실패", e); // 예외 발생 시 로그 출력
        }
    }

}