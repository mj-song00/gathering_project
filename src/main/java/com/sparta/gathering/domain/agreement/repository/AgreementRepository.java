package com.sparta.gathering.domain.agreement.repository;

import com.sparta.gathering.domain.agreement.entity.Agreement;
import com.sparta.gathering.domain.agreement.enums.AgreementType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AgreementRepository extends JpaRepository<Agreement, UUID> {

    @Query("SELECT a FROM Agreement a WHERE a.type = :type ORDER BY a.createdAt DESC")
    List<Agreement> findLatestAgreementByType(AgreementType type, Pageable pageable);

    boolean existsByTypeAndVersion(AgreementType type, String version);

    @Query("SELECT a FROM Agreement a WHERE a.id IN " +
            "(SELECT MAX(a2.id) FROM Agreement a2 GROUP BY a2.type)")
    List<Agreement> findAllLatestAgreements();

}
