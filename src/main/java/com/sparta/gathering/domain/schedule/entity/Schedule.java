package com.sparta.gathering.domain.schedule.entity;

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
@Table(name = "Schedule")
public class Schedule extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String scheduleTitle;

    @Column(nullable = false)
    private String scheduleContent;

    @Column
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gather_id")
    private Gather gather;

    public Schedule(String scheduleTitle, String scheduleContent, Gather gather) {
        this.gather = gather;
        this.scheduleTitle = scheduleTitle;
        this.scheduleContent = scheduleContent;
    }

    public void update(String scheduleTitle, String scheduleContent, Gather gather) {
        this.gather = gather;
        this.scheduleTitle = scheduleTitle;
        this.scheduleContent = scheduleContent;
    }

    public void delete(LocalDateTime now) {
        this.deletedAt = now;
    }

}
