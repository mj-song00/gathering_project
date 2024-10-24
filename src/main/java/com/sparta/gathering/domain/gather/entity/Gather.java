package com.sparta.gathering.domain.gather.entity;


import com.sparta.gathering.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    public Gather(String title){
        this.title = title;
    }

    public void updateGatherTitle(String title){
        this.title = title;
    }

    public void delete(){
        this.deletedAt = LocalDateTime.now();
    }
}
