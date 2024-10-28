package com.sparta.gathering.domain.comment.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@NoArgsConstructor
@Getter
@Table(name = "comment")
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String nickName;

    private String comment;//댓글

    private LocalDateTime deleteAt;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Schedule schedule;

    public Comment(User user,String comment,Schedule schedule){
        this.user= user;
        this.nickName = user.getNickName();
        this.comment = comment;
        this.schedule = schedule;
    }
    public void update(String comment){
        this.comment = comment ;
    }
    public void delete(){
        this.deleteAt = LocalDateTime.now();
    }
}

