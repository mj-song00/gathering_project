package com.sparta.gathering.domain.useragreement.repository;

import com.sparta.gathering.domain.agreement.entity.Agreement;
import com.sparta.gathering.domain.agreement.enums.AgreementStatus;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.useragreement.entity.UserAgreement;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserAgreementRepository extends JpaRepository<UserAgreement, UUID> {

    Optional<UserAgreement> findByUserIdAndAgreementId(UUID userId, UUID agreementId);

    Optional<UserAgreement> findByUserAndAgreement(User user, Agreement agreement);

    List<UserAgreement> findByAgreementIdAndStatus(UUID agreementId, AgreementStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE UserAgreement ua SET ua.status = 'EXPIRED' WHERE ua.status = 'PENDING_REAGREE' AND ua.updatedAt < :expirationDateTime")
    int expireOldPendingReAgreements(LocalDateTime expirationDateTime);

    @Query("SELECT ua FROM UserAgreement ua WHERE ua.status = 'PENDING_REAGREE' AND ua.updatedAt < :expirationDateTime")
    List<UserAgreement> findPendingReAgreementsOlderThan(LocalDateTime expirationDateTime);

    @Query("SELECT ua FROM UserAgreement ua WHERE ua.status = :status")
    List<UserAgreement> findByStatus(AgreementStatus status);

}
