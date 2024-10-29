package com.sparta.gathering.domain.board.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.gather.entity.Gather;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "boards")
public class Board extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String boardTitle;

    @Column(nullable = false)
    private String boardContent;

    @Column
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gather_id", nullable = false)
    private Gather gather;

    public Board(String boardTitle, String boardContent) {
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
    }

    public void update(String boardTitle, String boardContent) {
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
    }

    public void delete(LocalDateTime now){
        this.deletedAt = now;
    }
}
