package com.sparta.gathering.domain.gather.entity;


import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.category.entity.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name = "gather")
@Getter
public class Gather extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public Gather(String title, Category category){
        this.title = title;
        this.category = category;
    }

    public void updateGatherTitle(String title){
        this.title = title;
    }

    public void delete(){
        this.deletedAt = LocalDateTime.now();
    }
}
