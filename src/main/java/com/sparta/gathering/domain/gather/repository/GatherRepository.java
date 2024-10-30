package com.sparta.gathering.domain.gather.repository;

import com.sparta.gathering.domain.gather.entity.Gather;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GatherRepository extends JpaRepository<Gather, Long> {

    @Query("SELECT DISTINCT g FROM Gather g " +
            "JOIN FETCH g.category c " +
            "LEFT JOIN FETCH g.hashTagList h " +
            "WHERE g.deletedAt IS NULL " +
            "AND g.category.id = :categoryId " +
            "ORDER BY g.createdAt DESC")
    Page<Gather> findByCategoryWithHashTags(@Param("categoryId") Pageable pageable, Long categoryId);


    @Query("SELECT g FROM Gather g LEFT JOIN FETCH g.hashTagList WHERE g.title LIKE %:keyword%")
    Page<Gather> findByTitleContaining(@Param("keyword") Pageable pageable, String keyword);
}
