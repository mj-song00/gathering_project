package com.sparta.gathering.domain.map.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.map.entity.Map;
import com.sparta.gathering.domain.map.repository.MapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final RestTemplate restTemplate;
    private final HttpHeaders headers = new HttpHeaders();
    private final MapRepository mapRepository;
    private final ObjectMapper objectMapper;

    @Value("${kakao.map.api-key}")
    private String appKey;

    public ResponseEntity<String> searchMap(String searchMap) {

        // Set up headers
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("Authorization", appKey);

        // Create entity with headers
        HttpEntity<String> entity = new HttpEntity<>("parameters",headers);
        String rawURI = "https://dapi.kakao.com/v2/local/search/address.json?query=" + searchMap;

        return restTemplate.exchange(rawURI, HttpMethod.GET, entity, String.class);
    }

    /**
     * 주소와 위경도 저장 api
     * @param saveMap 저장할 주소 입력
     */
    @Transactional
    public void saveMap(String saveMap) {

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", appKey);

        HttpEntity<String> entity = new HttpEntity<>("parameters",headers);
        String rawURI = "https://dapi.kakao.com/v2/local/search/address.json?query=" + saveMap;
        ResponseEntity<String> response = restTemplate.exchange(rawURI, HttpMethod.GET, entity, String.class);
        try {
            JsonNode mapData = objectMapper.readTree(response.getBody()); //String 을 json형태로 변환

            //json에서 주소와 위경도를 뽑음
            String addressName = mapData.get("documents").get(0).get("address_name").asText();
            String latitude = mapData.get("documents").get(0).get("y").asText();
            String longitude = mapData.get("documents").get(0).get("x").asText();

            //entity에 저장
            Map map = new Map(addressName,latitude,longitude);
            mapRepository.save(map);

        } catch (JsonMappingException e) {
            throw new BaseException(ExceptionEnum.PERMISSION_DENIED_ROLE);
        } catch (JsonProcessingException e) {
            throw new BaseException(ExceptionEnum.JSON_TYPE_MISMATCH);
        }
    }
}
