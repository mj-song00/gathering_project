package com.sparta.gathering.domain.gather.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.board.entity.Board;
import com.sparta.gathering.domain.category.entity.Category;
import com.sparta.gathering.domain.hashtag.entity.HashTag;
import com.sparta.gathering.domain.schedule.entity.Schedule;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
  @JsonIgnore
  private Category category;


  @OneToMany(mappedBy = "gather", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
  @JsonIgnore
  private List<Board> boardList = new ArrayList<>();


  @OneToMany(mappedBy = "gather", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
  @JsonIgnore
  private List<Schedule> scheduleList = new ArrayList<>();

  @OneToMany(mappedBy = "gather", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<HashTag> hashTagList = new ArrayList<>();


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

  public void updateGatherTitle(String title) {
    this.title = title;
  }

  public void delete() {
    this.deletedAt = LocalDateTime.now();
  }

}
