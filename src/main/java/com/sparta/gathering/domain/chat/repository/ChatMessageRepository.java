package com.sparta.gathering.domain.chat.repository;

import com.sparta.gathering.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 특정 방의 채팅 메시지를 조회합니다.
     *
     * @param roomId   조회할 채팅방의 ID
     * @param pageable 페이지 정보
     * @return
     */
    Page<ChatMessage> findByRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);

}