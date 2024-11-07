package com.sparta.gathering.domain.gather.repository;

import com.sparta.gathering.domain.gather.entity.Gather;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface GatherRepository extends JpaRepository<Gather, Long>, GatherCustomRepository {

    @Query("SELECT g FROM Gather g " +
            "JOIN FETCH g.category c " +
            "LEFT JOIN FETCH g.hashTagList h " +
            "WHERE g.category.id = :categoryId " +
            "AND g.deletedAt IS NULL " +
            "ORDER BY g.createdAt DESC")
    Page<Gather> findByCategoryWithHashTags(@Param("categoryId") Pageable pageable, Long categoryId);
}
