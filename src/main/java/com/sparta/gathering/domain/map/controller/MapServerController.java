package com.sparta.gathering.domain.map.controller;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * 네이버 검색 API를 통해 장소 정보를 검색하는 RestController입니다.
 * 네이버 API를 호출하여 지정된 이름 또는 동적으로 지정된 검색어를 기반으로 장소를 검색하고, 검색된 장소 목록을 반환합니다.
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/sever")
public class MapServerController {
    @Value("${naver.client-id}")
    private String NAVER_API_ID;
    @Value("${naver.secret}")
    private String NAVER_API_SECRET;

    /**
     * 네이버 검색 API를 이용하여 지정된 이름으로 장소를 검색합니다.
     *
     * @param name 검색할 장소의 이름
     * @return 검색된 장소 목록
     */
    @GetMapping("/naver/{name}")
    public List<Map<String, String>> naver(@Valid @PathVariable String name) {
        return searchRestaurant(name);
    }
    /**
     * 네이버 검색 API를 이용하여 동적으로 검색어를 지정하여 장소를 검색합니다.
     *
     * @param query 동적으로 지정된 검색어
     * @return 검색된 장소 목록
     */
    @GetMapping("/naver")
    public List<Map<String, String>> naverSearchDynamic(@Valid @RequestParam String query) {
        return searchRestaurant(query);
    }
    /**
     * 네이버 검색 API를 이용하여 장소를 검색하는 메서드입니다.
     *
     * @param query 검색할 장소의 이름 또는 검색어
     * @return 검색된 장소 목록
     */
    private List<Map<String, String>> searchRestaurant(String query) {
        List<Map<String, String>> restaurants = new ArrayList<>();
        try {
            // UTF-8로 인코딩된 검색어 생성
            ByteBuffer buffer = StandardCharsets.UTF_8.encode(query);
            String encode = StandardCharsets.UTF_8.decode(buffer).toString();
            // 네이버 검색 API를 호출하기 위한 URI 생성
            URI uri = UriComponentsBuilder.fromUriString("https://openapi.naver.com").path("/v1/search/local")
                    .queryParam("query", encode).queryParam("display", 10).queryParam("start", 1)
                    .queryParam("sort", "random").encode().build().toUri();
            // RestTemplate을 사용하여 네이버 API에 요청을 보냄
            RestTemplate restTemplate = new RestTemplate();
            RequestEntity<Void> req = RequestEntity.get(uri).header("X-Naver-Client-Id", NAVER_API_ID)
                    .header("X-Naver-Client-Secret", NAVER_API_SECRET).build();
            // API 응답 데이터를 JSON 형식으로 변환
            ResponseEntity<String> response = restTemplate.exchange(req, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            // 검색 결과 중에서 장소 정보를 추출하여 리스트에 저장
            JsonNode itemsNode = rootNode.path("items");
            for (JsonNode itemNode : itemsNode) {
                Map<String, String> restaurant = new HashMap<>();
                restaurant.put("title", itemNode.path("title").asText()); // 장소 이름
                restaurant.put("address", itemNode.path("address").asText()); // 장소 주소
                /*
                 * restaurant.put("mapx", itemNode.path("mapx").asText());
                 * restaurant.put("mapy", itemNode.path("mapy").asText());
                 */
                // 위도와 경도를 double 형식으로 변환하여 저장
                double latitude = Double.parseDouble(itemNode.path("mapy").asText()) / 1e7; // 위도
                double longitude = Double.parseDouble(itemNode.path("mapx").asText()) / 1e7; // 경도
                restaurant.put("latitude", String.valueOf(latitude));
                restaurant.put("longitude", String.valueOf(longitude));
                restaurants.add(restaurant); // 리스트에 추가
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restaurants;
    }
}