package com.sparta.gathering.domain.map.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.map.dto.request.MapRequest;
import com.sparta.gathering.domain.map.dto.request.SearchGatherRequest;
import com.sparta.gathering.domain.map.dto.response.AroundPlaceResponse;
import com.sparta.gathering.domain.map.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class KakaoMapController {

    private final KakaoService kakaoService;

    /**
     * 카카오맵 지번으로 검색
     *
     * @param searchMap 검색할 주소
     */
    @GetMapping("/api/kakaoMap")
    public ResponseEntity<String> searchMap(@RequestBody MapRequest searchMap) {
        return kakaoService.searchMap(searchMap);
    }

    /**
     *
     * @param gatherId 모임의 아이디
     * @param saveMap
     * @return
     */
    @PostMapping("/api/kakaoMap/{gatherId}")
    public ResponseEntity<ApiResponse<Void>> saveMap(@PathVariable Long gatherId, @RequestBody MapRequest saveMap) {
        kakaoService.saveMap(saveMap,gatherId);
        ApiResponse<Void> response = ApiResponse.successWithOutData(
                ApiResponseEnum.SAVED_MAP_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 위치기반 주변 모임추천 api
     * @param request 위도 경도 위치를 받아서
     * @return 주변 반경 2km 모임을 추천 한다.
     */
    @GetMapping("/api/kakaoMap/search")
    public ResponseEntity<ApiResponse<List<AroundPlaceResponse>>> myMapList(
            @RequestBody SearchGatherRequest request
            ) {
        List<AroundPlaceResponse> list = kakaoService.listMyMap(request.getLongitude(), request.getLatitude());
        ApiResponse<List<AroundPlaceResponse>> response = ApiResponse.successWithData(list, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
