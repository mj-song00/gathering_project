package com.sparta.gathering.domain.member.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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