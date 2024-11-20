package com.sparta.gathering.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Getter
@Table(name = "batch_job_request")
public class BatchJobRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Column(name = "job_param")
    private String jobParam;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setJobParam(String jobParam) {
        this.jobParam = jobParam;
    }

    public void setRequestedAt(LocalDateTime now) {
        this.requestedAt = now;
    }
}