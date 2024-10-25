package com.sparta.gathering.domain.comment.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.user.entity.User;
import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String nickName;

    private String comment;//댓글

    private Boolean isDelete;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Schedule schedule;

    public Comment(User user,String comment){
        this.user = user;
        this.nickName = user.getNickName();
        this.comment = comment;
    }
    public void update(String comment){
        this.comment = comment ;
    }
    public void delete(){
        this.isDelete = false;
    }
}

