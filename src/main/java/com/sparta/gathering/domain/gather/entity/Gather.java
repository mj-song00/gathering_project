package com.sparta.gathering.domain.gather.entity;


import com.sparta.gathering.common.entity.Timestamped;
<<<<<<< HEAD
import com.sparta.gathering.domain.board.entity.Board;
import com.sparta.gathering.domain.schedule.entity.Schedule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

=======
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

>>>>>>> 60c6881a7279a7b423ceb3089bfd2ecd1bf35146
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

<<<<<<< HEAD
    @OneToMany(mappedBy = "gather", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Board> boardList = new ArrayList<>();


    @OneToMany(mappedBy = "gather", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Schedule> scheduleList = new ArrayList<>();

    public Gather(String title){
        this.title = title;
    }
=======
  private LocalDateTime deletedAt;
>>>>>>> 60c6881a7279a7b423ceb3089bfd2ecd1bf35146

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

<<<<<<< HEAD
    public void delete(){
        this.deletedAt = LocalDateTime.now();
    }

=======
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
>>>>>>> 60c6881a7279a7b423ceb3089bfd2ecd1bf35146
}
