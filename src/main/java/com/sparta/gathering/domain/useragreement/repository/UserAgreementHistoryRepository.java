package com.sparta.gathering.domain.useragreement.repository;

import com.sparta.gathering.domain.useragreement.entity.UserAgreementHistory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAgreementHistoryRepository extends JpaRepository<UserAgreementHistory, UUID> {

}
