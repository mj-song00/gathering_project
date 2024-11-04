package com.sparta.gathering.domain.gather.entity;


import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.board.entity.Board;
import com.sparta.gathering.domain.category.entity.Category;
import com.sparta.gathering.domain.schedule.entity.Schedule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor
@Table(name = "gather")
@Getter
public class Gather extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;


    @OneToMany(mappedBy = "gather", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Board> boardList = new ArrayList<>();


    @OneToMany(mappedBy = "gather", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Schedule> scheduleList = new ArrayList<>();

    public Gather(String title) {
        this.title = title;
    }

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


    public Gather(String title, String description, Category category) {
        this.title = title;
        this.description = description;
        this.category = category;
    }

    public void updateGatherTitle(String title) {
        this.title = title;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

}
