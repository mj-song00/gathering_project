package com.sparta.gathering.domain.member.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name = "member")
@Getter
@Setter
public class Member extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Permission permission;

    @Column
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "gather_id", nullable = false)
    private Gather gather;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Member(User user, Gather gather, Permission permission) {
        this.user = user;
        this.gather = gather;
        this.permission = permission;
    }

    public void updatePermission(Permission permission) {
        this.permission = permission;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}