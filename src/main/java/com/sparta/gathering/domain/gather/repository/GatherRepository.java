package com.sparta.gathering.domain.gather.repository;

import com.sparta.gathering.domain.gather.entity.Gather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface GatherRepository extends JpaRepository<Gather, Long>, GatherCustomRepository {

    // 생성일 기준 내림차순 정렬 후 상위 5개 모임 조회
    List<Gather> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT g FROM Gather g JOIN fetch Member m ON g.id = m.gather.id WHERE m.user.id = :userId")
    List<Gather> findAllByUserId(@Param("userId") UUID userId);
}
