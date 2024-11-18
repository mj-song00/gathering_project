package com.sparta.gathering.domain.useragreement.entity;

import com.sparta.gathering.domain.agreement.enums.AgreementStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_agreement_history")
public class UserAgreementHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID agreementId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgreementStatus status;

    @Column(nullable = false)
    private LocalDateTime agreedAt;

    public UserAgreementHistory(UUID userId, UUID agreementId, AgreementStatus status, LocalDateTime agreedAt) {
        this.userId = userId;
        this.agreementId = agreementId;
        this.status = status;
        this.agreedAt = agreedAt;
    }

}
