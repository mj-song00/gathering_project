package com.sparta.gathering.domain.gather.repository;

import com.sparta.gathering.domain.gather.entity.Gather;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface GatherRepository extends JpaRepository<Gather, Long>, GatherCustomRepository {

    @Query("SELECT g FROM Gather g " +
            "JOIN FETCH g.category c " +
            "LEFT JOIN FETCH g.hashTagList h " +
            "WHERE g.category.id = :categoryId " +
            "AND g.deletedAt IS NULL " +
            "ORDER BY g.createdAt DESC")
    Page<Gather> findByCategoryWithHashTags(@Param("categoryId") Pageable pageable, Long categoryId);

    // 생성일 기준 내림차순 정렬 후 상위 5개 모임 조회
    List<Gather> findTop5ByOrderByCreatedAtDesc();

}
