package com.sparta.gathering.domain.gather.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.gather.dto.request.GatherRequest;
import com.sparta.gathering.domain.gather.dto.response.GatherListResponse;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.service.GatherService;
import com.sparta.gathering.domain.user.entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Gather", description = "소모임 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gathers")
public class GatherController {
    private final GatherService gatherService;

    @PostMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> createGather(
            @RequestBody GatherRequest request,
            @AuthenticationPrincipal User user,
            @PathVariable UUID categoryId
    ) {
        gatherService.createGather(request, user, categoryId);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.GATHER_CREATE_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //모임 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> modifyGather(
            @RequestBody GatherRequest request,
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ){
        gatherService.modifyGather(request, id, user);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.GATHER_CREATE_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    //@모임 삭제(soft delete)
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGather(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ){
        gatherService.deleteGather(id, user);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.GATHER_DELETE_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/{categoryId}")
    public ApiResponse<GatherListResponse> Gathers(
            @RequestParam(defaultValue ="1") int page,
            @PathVariable UUID categoryId )

    {
        Pageable pageable = PageRequest.of(page-1, 10);

        Page<Gather> gatherList = gatherService.Gathers(pageable, categoryId);
        GatherListResponse response = new GatherListResponse(
                gatherList.getContent(), // Gather 리스트
                gatherList.getNumber(), // 현재 페이지 번호
                gatherList.getTotalPages(), // 총 페이지 수
                gatherList.getTotalElements() // 총 요소 수
        );
        return ApiResponse.successWithData(response, ApiResponseEnum.GET_SUCCESS);
    }
}
