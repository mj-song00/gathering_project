package com.sparta.gathering.domain.agreement.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.agreement.enums.AgreementType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agreement_history")
@Getter
@NoArgsConstructor
public class AgreementHistory extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID agreementId;  // 기존 Agreement와 매핑하기 위한 참조 ID

    @Column(nullable = false)
    private String content;    // 이전 약관 내용

    @Column(nullable = false)
    private String version;    // 이전 약관 버전 정보

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgreementType type;

    public AgreementHistory(UUID agreementId, String content, String version, AgreementType type) {
        this.agreementId = agreementId;
        this.content = content;
        this.version = version;
        this.type = type;
    }
}

