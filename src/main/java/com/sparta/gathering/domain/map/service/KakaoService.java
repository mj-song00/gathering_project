package com.sparta.gathering.domain.map.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.gathering.domain.map.entity.Map;
import com.sparta.gathering.domain.map.repository.MapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final RestTemplate restTemplate;
    private final HttpHeaders headers = new HttpHeaders();
    private final MapRepository mapRepository;
    private final ObjectMapper objectMapper;

    public ResponseEntity<String> searchMap(String searchMap) {

        // Set up headers
        headers.setContentType(MediaType.APPLICATION_JSON);
        String appkey = "KakaoAK fe5907af108eb89d33ecb50f39844b0e";//
        headers.set("Authorization", appkey);

        // Create entity with headers
        HttpEntity<String> entity = new HttpEntity<>("parameters",headers);
        String rawURI = "https://dapi.kakao.com/v2/local/search/address.json?query=" + searchMap;

        return restTemplate.exchange(rawURI, HttpMethod.GET, entity, String.class);
    }

    public void saveMap(String saveMap) {

        headers.setContentType(MediaType.APPLICATION_JSON);
        String appkey = "KakaoAK fe5907af108eb89d33ecb50f39844b0e";//
        headers.set("Authorization", appkey);

        HttpEntity<String> entity = new HttpEntity<>("parameters",headers);
        String rawURI = "https://dapi.kakao.com/v2/local/search/address.json?query=" + saveMap;
        ResponseEntity<String> response = restTemplate.exchange(rawURI, HttpMethod.GET, entity, String.class);
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode documents = root.path("documents");
            log.info(documents.path("y").toString());
            Map map = new Map(documents);
            mapRepository.save(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
