package com.sparta.gathering.domain.useragreement.repository;

import com.sparta.gathering.domain.agreement.entity.Agreement;
import com.sparta.gathering.domain.agreement.enums.AgreementStatus;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.useragreement.entity.UserAgreement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAgreementRepository extends JpaRepository<UserAgreement, UUID> {

    Optional<UserAgreement> findByUserIdAndAgreementId(UUID userId, UUID agreementId);

    Optional<UserAgreement> findByUserAndAgreement(User user, Agreement agreement);

    // 유예 기간 만료 검사를 위한 PENDING_REAGREE 상태 및 특정 날짜 이전 사용자 약관 조회
    List<UserAgreement> findByStatusAndAgreedAtBefore(AgreementStatus status, LocalDateTime agreedBeforeDate);

}
