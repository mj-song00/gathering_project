package com.sparta.gathering.domain.category.repository;

import com.sparta.gathering.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
