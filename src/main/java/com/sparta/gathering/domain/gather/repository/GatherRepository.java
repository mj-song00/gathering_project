package com.sparta.gathering.domain.gather.repository;

import com.sparta.gathering.domain.gather.entity.Gather;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GatherRepository extends JpaRepository<Gather, Long> {
    List<Gather> findByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);
}
