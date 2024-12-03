package com.sparta.gathering.common.repository;

import com.sparta.gathering.common.entity.BatchJobRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchJobRequestRepository extends JpaRepository<BatchJobRequest, Long> {

    List<BatchJobRequest> findByJobNameOrderByRequestedAt(String jobName);
}
