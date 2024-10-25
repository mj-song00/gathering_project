package com.sparta.gathering.domain.gather.repository;

import com.sparta.gathering.domain.gather.entity.Gather;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GatherRepository extends JpaRepository<Gather, Long> {
    @Query("SELECT g FROM Gather g WHERE g.deletedAt IS NULL AND g.category.id = :categoryId ORDER BY g.createdAt DESC")
    Page<Gather> findGathersByCategoryId(Pageable pageable, @Param("categoryId") UUID categoryId);
}
