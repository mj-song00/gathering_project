package com.sparta.gathering.domain.useragreement.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.agreement.entity.Agreement;
import com.sparta.gathering.domain.agreement.enums.AgreementStatus;
import com.sparta.gathering.domain.agreement.enums.AgreementType;
import com.sparta.gathering.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "user_agreements")
@NoArgsConstructor
public class UserAgreement extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agreement_id", nullable = false)
    private Agreement agreement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgreementStatus status;

    private LocalDateTime agreedAt;

    public UserAgreement(User user, Agreement agreement, AgreementStatus status) {
        this.user = user;
        this.agreement = agreement;
        this.status = status;
        this.agreedAt = LocalDateTime.now();
        user.getUserAgreements().add(this);
    }

    public void updateStatus(AgreementStatus newStatus) {
        this.status = newStatus;
        this.agreedAt = LocalDateTime.now();
    }

    public void setUser(User user) {
        this.user = user;
    }

    // 필수 약관인지 확인하는 메서드
    public boolean isRequired() {
        return agreement.getType() == AgreementType.PRIVACY_POLICY
                || agreement.getType() == AgreementType.TERMS_OF_SERVICE;
    }
}
