package com.sparta.gathering.domain.category.dto.response;

import com.sparta.gathering.domain.category.entity.Category;
import lombok.Getter;

import java.util.UUID;

@Getter

public class CategoryRes {
    private final UUID id;
    private final String categoryName;

    private CategoryRes(UUID id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    public static CategoryRes from(Category category) {
        return new CategoryRes(category.getId(), category.getCategoryName());
    }

}