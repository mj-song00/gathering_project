package com.sparta.gathering.domain.category.repository;

import com.sparta.gathering.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(String categoryName);

    List<Category> findByDeletedAtIsNull();
}
