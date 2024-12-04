package com.sparta.gathering.domain.chat.repository;

import com.sparta.gathering.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByRoom_IdOrderByCreatedAtDesc(Long roomId, Pageable pageable);

}