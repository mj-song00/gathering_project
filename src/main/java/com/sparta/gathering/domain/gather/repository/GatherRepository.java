package com.sparta.gathering.domain.gather.repository;

import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GatherRepository extends JpaRepository<Gather, Long> {
    List<Gather> findByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);
}
