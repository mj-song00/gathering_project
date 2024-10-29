package com.sparta.gathering.domain.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@RequiredArgsConstructor
@RestController
public class KakaoMapController {

    private final RestTemplate restTemplate;

    @GetMapping("/api/kakaoMap/{searchMap}")
    public ResponseEntity<String> searchMap(@PathVariable String searchMap) throws UnsupportedEncodingException {
        String url = "https://dapi.kakao.com/v2/local/search/category.json?category_group_code=PM9&radius=20000";

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String appkey = "KakaoAK fe5907af108eb89d33ecb50f39844b0e";
        headers.set("Authorization", appkey);


        // Create entity with headers
        HttpEntity<String> entity = new HttpEntity<String>("parameters",headers);
        String encode = URLEncoder.encode(searchMap,"UTF-8");

        // Make the request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response ;
    }
}
