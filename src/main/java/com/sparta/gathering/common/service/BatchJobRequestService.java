package com.sparta.gathering.common.service;

import com.sparta.gathering.common.entity.BatchJobRequest;
import com.sparta.gathering.common.repository.BatchJobRequestRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BatchJobRequestService {

    private final BatchJobRequestRepository repository;

    public void createBatchJobRequest(String jobName, String jobParam) {
        BatchJobRequest request = new BatchJobRequest();
        request.setJobName(jobName);
        request.setJobParam(jobParam);
        request.setRequestedAt(LocalDateTime.now());
        repository.save(request);
    }

    public List<BatchJobRequest> getPendingRequests(String jobName) {
        return repository.findByJobNameOrderByRequestedAt(jobName);
    }

    public void deleteRequest(Long requestId) {
        repository.deleteById(requestId);
    }
}

