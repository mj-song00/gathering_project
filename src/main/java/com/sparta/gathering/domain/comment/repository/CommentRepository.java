package com.sparta.gathering.domain.comment.repository;

import com.sparta.gathering.domain.comment.entity.Comment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByScheduleIdAndDeletedAtIsNull(Long scheduleId, Pageable pageable);

    Optional<Comment> findByScheduleIdAndIdAndDeletedAtIsNull(Long scheduleId, Long commentId);
}
