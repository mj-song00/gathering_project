package com.sparta.gathering.domain.member.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.member.enums.Permission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name = "member")
@Getter
public class Member extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Permission permission;

    @Column
    private LocalDateTime deletedAt;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_id", nullable = false)
//    private Category category;

//    public Member Member (Permission permission){
//        this.permission = permission;
//    }
}