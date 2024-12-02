package com.sparta.gathering.domain.chat.repository;

import com.sparta.gathering.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByGatherIdOrderByTimestamp(Long gatheringId);
}
