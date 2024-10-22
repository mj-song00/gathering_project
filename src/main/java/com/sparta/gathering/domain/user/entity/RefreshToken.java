package com.sparta.gathering.domain.user.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RefreshToken")
public class RefreshToken {

    @Id
    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID id; // UUID를 BINARY(16)로 저장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User 엔티티와의 관계

    @Column(nullable = false, unique = true)
    private String refreshToken; // Refresh Token 값

    @Column(nullable = false)
    private java.sql.Timestamp expiryDate; // Refresh Token 만료 시간

    public RefreshToken(User user, String refreshToken, java.sql.Timestamp expiryDate) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.refreshToken = refreshToken;
        this.expiryDate = expiryDate;
    }

}
