package com.sparta.gathering.domain.category.dto.response;

import com.sparta.gathering.domain.category.entity.Category;
import lombok.Getter;

@Getter

public class CategoryRes {
    private final Long id;
    private final String categoryName;

    private CategoryRes(Long id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    public static CategoryRes from(Category category) {
        return new CategoryRes(category.getId(), category.getCategoryName());
    }

}