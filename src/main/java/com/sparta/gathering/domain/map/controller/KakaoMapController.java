package com.sparta.gathering.domain.map.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.map.dto.request.MapRequest;
import com.sparta.gathering.domain.map.dto.request.SearchGatherRequest;
import com.sparta.gathering.domain.map.dto.response.AroundPlaceResponse;
import com.sparta.gathering.domain.map.service.KakaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "KakaoMap", description = "카카오맵 API / 변영덕")
@RequiredArgsConstructor
@RestController
public class KakaoMapController {

    private final KakaoService kakaoService;

    /**
     * 카카오맵 지번으로 검색
     *
     * @param searchMap 검색할 주소
     */
    @Operation(summary = "카카오맵 검색", description = "카카오맵에서 주소를 검색합니다.")
    @GetMapping("/api/kakaoMap")
    public ResponseEntity<String> searchMap(@RequestBody MapRequest searchMap) {
        return kakaoService.searchMap(searchMap);
    }

    /**
     * @param gatherId 모임의 아이디
     * @param saveMap
     * @return
     */
    @Operation(summary = "모임에 지도 저장", description = "모임에 지도를 저장합니다.")
    @PostMapping("/api/kakaoMap/{gatherId}")
    public ResponseEntity<ApiResponse<Void>> saveMap(@PathVariable Long gatherId, @RequestBody MapRequest saveMap) {
        kakaoService.saveMap(saveMap, gatherId);
        ApiResponse<Void> response = ApiResponse.successWithOutData(
                ApiResponseEnum.SAVED_MAP_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 위치기반 주변 모임추천 api
     *
     * @param request 위도 경도 위치를 받아서
     * @return 주변 반경 2km 모임을 추천 한다.
     */
    @Operation(summary = "위치기반 주변 모임추천", description = "위치기반 주변 모임을 추천합니다.")
    @GetMapping("/api/kakaoMap/search")
    public ResponseEntity<ApiResponse<List<AroundPlaceResponse>>> myMapList(
            @RequestBody SearchGatherRequest request
    ) {
        List<AroundPlaceResponse> list = kakaoService.listMyMap(request.getLongitude(), request.getLatitude(),
                request.getDistance());
        ApiResponse<List<AroundPlaceResponse>> response = ApiResponse.successWithData(list,
                ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 위치기반 주변 모임추천 api
     * <p>
     * 래디스의 지오 스페셜 자료구조를 활용 하여 조회 성능을 높인 api
     *
     * @param request 사용자의 위경도 정보와 추천 범위를 지정할 distance 값을 받아온다.
     * @return 반경 distance 만큼 모임을 추천 받는다.
     */
    @Operation(summary = "위치기반 주변 모임추천", description = "위치기반 주변 모임을 추천합니다.")
    @GetMapping("/api/kakaoMap/RedisSearch")
    public ResponseEntity<ApiResponse<List<AroundPlaceResponse>>> redisSearch(
            @RequestBody SearchGatherRequest request
    ) {
        List<AroundPlaceResponse> list = kakaoService.nearByVenues(request.getLongitude(), request.getLatitude(),
                request.getDistance());
        ApiResponse<List<AroundPlaceResponse>> response = ApiResponse.successWithData(list,
                ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
