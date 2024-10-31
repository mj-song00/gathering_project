package com.sparta.gathering.domain.map.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.map.dto.request.MapRequest;
import com.sparta.gathering.domain.map.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class KakaoMapController {

    private final KakaoService kakaoService;

    /**
     * 카카오맵 지번으로 검색
     * @param searchMap 검색할 주소
     */
    @GetMapping("/api/kakaoMap")
    public ResponseEntity<String> searchMap(@RequestBody MapRequest searchMap) {
        return kakaoService.searchMap(searchMap);
    }

    @PostMapping("/api/kakaoMap")
    public ResponseEntity<ApiResponse<Void>> saveMap(@RequestBody MapRequest saveMap) {
        kakaoService.saveMap(saveMap);
        ApiResponse<Void> response = ApiResponse.successWithOutData(
                ApiResponseEnum.SAVED_MAP_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
