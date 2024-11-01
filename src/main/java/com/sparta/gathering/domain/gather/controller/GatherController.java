package com.sparta.gathering.domain.gather.controller;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.gather.dto.request.GatherRequest;
import com.sparta.gathering.domain.gather.dto.response.GatherListResponse;
import com.sparta.gathering.domain.gather.dto.response.SearchResponse;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.service.GatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Gather", description = "소모임 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gathers")
public class GatherController {

    private final GatherService gatherService;

    @Operation(summary = "소모임 생성", description = "모임을 생성합니다. 생성 즉시 모임의 매니저로 등록됩니다.")
    @PostMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> createGather(
            @RequestBody GatherRequest request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long categoryId
    ) {
        gatherService.createGather(request, authenticatedUser, categoryId);
        ApiResponse<Void> response = ApiResponse.successWithOutData(
                ApiResponseEnum.GATHER_CREATE_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "소모임 수정", description = "생성시 저장된 title, description, hashtag를 수정할 수 있습니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> modifyGather(
            @RequestBody GatherRequest request,
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        gatherService.modifyGather(request, id, authenticatedUser);
        ApiResponse<Void> response = ApiResponse.successWithOutData(
                ApiResponseEnum.GATHER_UPDATE_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "모임 삭제", description = "soft delete를 지원합니다.")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGather(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        gatherService.deleteGather(id, authenticatedUser);
        ApiResponse<Void> response = ApiResponse.successWithOutData(
                ApiResponseEnum.GATHER_DELETE_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "카테고리별 검색", description = "카테고리별로 모임조회사 가능합니다." +
            "page size는 10입니다.")
    @GetMapping("/{categoryId}")
    public ApiResponse<GatherListResponse> gathers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long categoryId) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Gather> gatherList = gatherService.gathers(pageable, categoryId);
        GatherListResponse response = new GatherListResponse(
                gatherList.getContent(), // Gather 리스트
                gatherList.getNumber(), // 현재 페이지 번호
                gatherList.getTotalPages(), // 총 페이지 수
                gatherList.getTotalElements() // 총 요소 수
        );

        return ApiResponse.successWithData(response, ApiResponseEnum.GET_SUCCESS);
    }

    @Operation(summary = "title, hashtag 검색", description = "keyword로 title 혹은 hashtag를 가진 모임을 검색합니다." +
            "page size는 10입니다.")
    @GetMapping("/search")
    public ApiResponse<SearchResponse> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Gather> searchList = gatherService.findTitle(pageable, keyword);
        SearchResponse response = new SearchResponse(
                searchList.getContent(), // Gather 리스트
                searchList.getNumber(), // 현재 페이지 번호
                searchList.getTotalPages(), // 총 페이지 수
                searchList.getTotalElements() // 총 요소 수
        );
        return ApiResponse.successWithData(response, ApiResponseEnum.GET_SUCCESS);
    }
}
