package com.sparta.gathering.domain.board.repository;

import com.sparta.gathering.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
