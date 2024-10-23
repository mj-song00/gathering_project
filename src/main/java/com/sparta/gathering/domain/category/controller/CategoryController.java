package com.sparta.gathering.domain.category.controller;


import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.category.dto.request.CategoryReq;
import com.sparta.gathering.domain.category.dto.response.CategoryRes;
import com.sparta.gathering.domain.category.service.CategoryService;
import com.sparta.gathering.domain.user.entity.RefreshToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // 카테고리 생성
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryRes>> createCategory(
            @AuthenticationPrincipal RefreshToken token,
            @Valid @RequestBody CategoryReq categoryReq) {
        CategoryRes res = categoryService.createCategory(token, categoryReq);
        ApiResponse<CategoryRes> response = ApiResponse.successWithData(res, ApiResponseEnum.SIGNUP_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 카테고리 삭제
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<?>> deleteCategory(
            @AuthenticationPrincipal RefreshToken token,
            @PathVariable UUID categoryId) {
        categoryService.deleteCategory(token, categoryId);
        ApiResponse<?> response = ApiResponse.successWithoutData(ApiResponseEnum.SIGNUP_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}