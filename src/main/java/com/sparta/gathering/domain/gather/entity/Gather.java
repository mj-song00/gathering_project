package com.sparta.gathering.domain.gather.entity;


import com.sparta.gathering.common.entity.Timestamped;

import com.sparta.gathering.domain.board.entity.Board;
import com.sparta.gathering.domain.schedule.entity.Schedule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.sparta.gathering.domain.category.entity.Category;
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

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


    @OneToMany(mappedBy = "gather", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Board> boardList = new ArrayList<>();


    @OneToMany(mappedBy = "gather", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Schedule> scheduleList = new ArrayList<>();

    public Gather(String title) {
        this.title = title;
    }

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
