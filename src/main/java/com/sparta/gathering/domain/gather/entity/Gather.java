package com.sparta.gathering.domain.gather.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.board.entity.Board;
import com.sparta.gathering.domain.category.entity.Category;
import com.sparta.gathering.domain.hashtag.entity.HashTag;
import com.sparta.gathering.domain.map.entity.Map;
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

    @Column(unique=true)
    private String title;
    private String description;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;


    @OneToMany(mappedBy = "gather", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Board> boardList = new ArrayList<>();


    @OneToMany(mappedBy = "gather", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Schedule> scheduleList = new ArrayList<>();

    @OneToMany(mappedBy = "gather", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HashTag> hashTagList = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "map_id", unique = true)
    private Map map;

    public Gather(String title) {
        this.title = title;
    }

    public Gather(String title, String description, Category category, List<String> hashtags) {
        this.title = title;
        this.description = description;
        this.category = category;
        // 해시태그 객체 생성 및 양방향 관계 설정
        for (String hashTagName : hashtags) {
            this.hashTagList.add(HashTag.of(hashTagName, this));
        }
    }

    public void updateGather(String title, String description, List<String> hashtags, Map map) {
        this.title = title;
        this.description = description;
        for (String hashTagName : hashtags) {
            this.hashTagList.add(HashTag.of(hashTagName, this));
        }
        this.map = map;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void saveMap(Map map) {
        this.map = map;
        if (map.getGather() != this) {
            map.saveGather(this);
        }
    }
}
