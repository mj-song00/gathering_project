package com.sparta.gathering.domain.gather.controller;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.gather.dto.request.GatherRequest;
import com.sparta.gathering.domain.gather.dto.response.CategoryListResponse;
import com.sparta.gathering.domain.gather.dto.response.GatherResponse;
import com.sparta.gathering.domain.gather.dto.response.NewGatherResponse;
import com.sparta.gathering.domain.gather.dto.response.RankResponse;
import com.sparta.gathering.domain.gather.dto.response.SearchResponse;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.service.GatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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

@Tag(name = "Gather", description = "소모임 / 송민지")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gathers")
//@CrossOrigin(origins = "http://localhost:63342")
public class GatherController {

    private final GatherService gatherService;

    @Operation(summary = "소모임 생성", description = "모임을 생성합니다. 생성 즉시 모임의 매니저로 등록됩니다.")
    @PostMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> createGather(
            @Valid @RequestBody GatherRequest request,
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
            @Valid @RequestBody GatherRequest request,
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
    public ResponseEntity<ApiResponse<CategoryListResponse>> gathers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long categoryId) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Gather> gatherList = gatherService.gathers(pageable, categoryId);
        CategoryListResponse response = new CategoryListResponse(
                gatherList.getContent(), // Gather 리스트
                gatherList.getNumber(), // 현재 페이지 번호
                gatherList.getTotalPages(), // 총 페이지 수
                gatherList.getTotalElements() // 총 요소 수
        );
        ApiResponse<CategoryListResponse> apiResponse = ApiResponse.successWithData(response,
                ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @Operation(summary = "hashtag 검색", description = "2개 이상의 keyword로 hashtag를 가진 모임을 검색합니다." +
            "page size는 10입니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<SearchResponse>> search(
            @RequestParam(value = "hashTagName", required = false) List<String> hashTagName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Gather> searchList = gatherService.findByHashTags(pageable, hashTagName);
        SearchResponse response = new SearchResponse(
                searchList.getContent(), // Gather 리스트
                searchList.getNumber(), // 현재 페이지 번호
                searchList.getTotalPages(), // 총 페이지 수
                searchList.getTotalElements() // 총 요소 수
        );
        ApiResponse<SearchResponse> apiResponse = ApiResponse.successWithData(response, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    //title 검색
    @Operation(summary = "title 검색", description = "contain 검색 방식입니다." +
            "page size는 10입니다.")
    @GetMapping("/title")
    public ResponseEntity<ApiResponse<SearchResponse>> searchTitles(
            @RequestParam(value = "title") String title,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Gather> titleList = gatherService.findByTitles(pageable, title);
        SearchResponse response = new SearchResponse(
                titleList.getContent(), // Gather 리스트
                titleList.getNumber(), // 현재 페이지 번호
                titleList.getTotalPages(), // 총 페이지 수
                titleList.getTotalElements() // 총 요소 수
        );
        ApiResponse<SearchResponse> apiResponse = ApiResponse.successWithData(response, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @Operation(summary = "상위 5개 랭킹 조회", description = "Redis에 저장된 상위 5개 랭킹을 조회합니다.")
    @GetMapping("/topRanking")
    public ResponseEntity<ApiResponse<List<RankResponse>>> getTop5Ranking() {
        List<RankResponse> top5RankingList = gatherService.getTop5Ranking();
        return ResponseEntity.ok(ApiResponse.successWithData(top5RankingList,
                ApiResponseEnum.GET_SUCCESS));
    }

    //gather 상세조회
    @Operation(summary = "모임 상세조회", description = "모임 상세조회 페이지 입니다")
    @GetMapping("/{gatherId}/detail") // 상세페이지
    public ResponseEntity<ApiResponse<GatherResponse>> detailGather(
            @PathVariable Long gatherId
    ) {
        GatherResponse responseItem = gatherService.getDetails(gatherId);
        return ResponseEntity.ok(ApiResponse.successWithData(responseItem, ApiResponseEnum.GET_SUCCESS));
    }


    @Operation(summary = "새로 생긴 모임 조회", description = "최근에 새로 생긴 모임 목록 5개 조회 됩니다.")
    @GetMapping("/newGather")
    public ResponseEntity<ApiResponse<List<NewGatherResponse>>> newCreatedGatherList() {
        List<NewGatherResponse> list = gatherService.newCreatedGatherList();
        return ResponseEntity.ok(ApiResponse.successWithData(list,
                ApiResponseEnum.GET_SUCCESS));
    }

    @Operation(summary = "멤버로 있는 모임 조회", description = "본인이 가입되어있는 모임이 조회 됩니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<GatherListResponse>>> getGatherList(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        List<GatherListResponse> list = gatherService.getGatherList(authenticatedUser);
        return ResponseEntity.ok(ApiResponse.successWithData(list, ApiResponseEnum.GET_SUCCESS));
    }
}
