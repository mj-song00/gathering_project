package com.sparta.gathering.domain.category.dto.response;

import com.sparta.gathering.domain.category.entity.Category;
import lombok.Getter;

import java.util.UUID;

@Getter

public class CategoryRes {
    private final UUID id;
    private final String title;

    private CategoryRes(UUID id, String title) {
        this.id = id;
        this.title = title;
    }

    public static CategoryRes from(Category category) {
        return new CategoryRes(category.getId(), category.getTitle());
    }

}