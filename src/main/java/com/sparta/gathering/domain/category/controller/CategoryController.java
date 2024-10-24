package com.sparta.gathering.domain.category.controller;


import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.category.dto.request.CategoryReq;
import com.sparta.gathering.domain.category.dto.response.CategoryRes;
import com.sparta.gathering.domain.category.service.CategoryService;
import com.sparta.gathering.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // 카테고리 생성
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryRes>> createCategory(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CategoryReq categoryReq) {
        CategoryRes res = categoryService.createCategory(user, categoryReq);
        ApiResponse<CategoryRes> response = ApiResponse.successWithData(res, ApiResponseEnum.CREATED_CATEGORY_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 카테고리 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryRes>>> getHashTagList() {
        List<CategoryRes> list = categoryService.getCategoryList();
        ApiResponse<List<CategoryRes>> response = ApiResponse.successWithData(list, ApiResponseEnum.GET_CATEGORY_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 카테고리 수정
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryRes>> updateCategory(
            @AuthenticationPrincipal User user,
            @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryReq categoryReq) {
        CategoryRes res = categoryService.updateCategory(user, categoryId, categoryReq);
        ApiResponse<CategoryRes> response = ApiResponse.successWithData(res, ApiResponseEnum.UPDATE_CATEGORY_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 카테고리 삭제
    @PatchMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<?>> deleteCategory(
            @AuthenticationPrincipal User token,
            @PathVariable UUID categoryId) {
        categoryService.deleteCategory(token, categoryId);
        ApiResponse<?> response = ApiResponse.successWithOutData(ApiResponseEnum.DELETED_CATEGORY_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}