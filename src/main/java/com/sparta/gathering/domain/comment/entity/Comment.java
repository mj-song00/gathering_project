package com.sparta.gathering.domain.comment.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.schedule.entity.Schedule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Getter
@Table(name = "comment")
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String comment;//댓글

    @Column
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "schedule_Id")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "member_Id")
    private Member member;


    public Comment(String comment, Schedule schedule, Member member) {
        this.nickName = member.getUser().getNickName();
        this.comment = comment;
        this.schedule = schedule;
        this.member = member;
    }

    public void update(String comment) {
        this.comment = comment;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}

