package com.sparta.gathering.domain.agreement.repository;

import com.sparta.gathering.domain.agreement.entity.Agreement;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AgreementRepository extends JpaRepository<Agreement, UUID> {

    @Query("SELECT a FROM Agreement a ORDER BY a.createdAt DESC")
    Optional<Agreement> findLatestAgreement();
}
