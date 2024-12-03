package com.sparta.gathering.domain.category.controller;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.category.dto.request.CategoryReq;
import com.sparta.gathering.domain.category.dto.response.CategoryRes;
import com.sparta.gathering.domain.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category", description = "카테고리 API / 고결")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 생성
    @Operation(summary = "카테고리 생성", description = "ADMIN 계정만 생성 가능합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryRes>> createCategory(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody CategoryReq categoryReq) {
        CategoryRes res = categoryService.createCategory(authenticatedUser, categoryReq);
        ApiResponse<CategoryRes> response = ApiResponse.successWithData(res,
                ApiResponseEnum.CREATED_CATEGORY_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 카테고리 조회
    @Operation(summary = "카테고리 조회", description = "카테고리 전체 조회 입니다")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryRes>>> getHashTagList() {
        List<CategoryRes> list = categoryService.getCategoryList();
        ApiResponse<List<CategoryRes>> response = ApiResponse.successWithData(list,
                ApiResponseEnum.GET_CATEGORY_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 카테고리 수정
    @Operation(summary = "카테고리 수정", description = "ADMIN 계정만 수정 가능합니다.")
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryRes>> updateCategory(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryReq categoryReq) {
        CategoryRes res = categoryService.updateCategory(authenticatedUser, categoryId, categoryReq);
        ApiResponse<CategoryRes> response = ApiResponse.successWithData(res,
                ApiResponseEnum.UPDATE_CATEGORY_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 카테고리 삭제
    @Operation(summary = "카테고리 삭제", description = "ADMIN 계정만 삭제 가능합니다.")
    @PatchMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<?>> deleteCategory(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long categoryId) {
        categoryService.deleteCategory(authenticatedUser, categoryId);
        ApiResponse<?> response = ApiResponse.successWithOutData(
                ApiResponseEnum.DELETED_CATEGORY_SUCCESS);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}