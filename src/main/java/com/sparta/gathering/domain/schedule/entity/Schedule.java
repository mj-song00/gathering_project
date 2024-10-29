package com.sparta.gathering.domain.schedule.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.gather.entity.Gather;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "Schedules")
public class Schedule extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String scheduleTitle;

    @Column(nullable = false)
    private String scheduleContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gather_id")
    private Gather gather;

    public Schedule(String scheduleTitle, String scheduleContent) {
        this.scheduleTitle = scheduleTitle;
        this.scheduleContent = scheduleContent;
    }

    public void update(String scheduleTitle, String scheduleContent) {
        this.scheduleTitle = scheduleTitle;
        this.scheduleContent = scheduleContent;
    }

    public void setGather(Gather gather) {
        this.gather = gather;
    }
}
