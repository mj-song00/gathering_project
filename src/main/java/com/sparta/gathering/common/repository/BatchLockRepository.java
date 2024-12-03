package com.sparta.gathering.common.repository;

import com.sparta.gathering.common.entity.BatchLock;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchLockRepository extends JpaRepository<BatchLock, Long> {

    Optional<BatchLock> findByJobName(String jobName);
}