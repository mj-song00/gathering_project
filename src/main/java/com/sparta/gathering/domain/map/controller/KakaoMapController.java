package com.sparta.gathering.domain.map.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.domain.map.service.KakaoService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.parser.ParseException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@RestController
public class KakaoMapController {

    private final RestTemplate restTemplate;
    private final KakaoService kakaoService;

    @GetMapping("/api/kakaoMap/{searchMap}")
    public ResponseEntity<String> searchMap(@PathVariable String searchMap) throws UnsupportedEncodingException, URISyntaxException, ParseException {

        kakaoService.searchMap();
        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String appkey = "KakaoAK fe5907af108eb89d33ecb50f39844b0e";//
        headers.set("Authorization", appkey);

        // Create entity with headers
        HttpEntity<String> entity = new HttpEntity<String>("parameters",headers);
        String rawURI = "https://dapi.kakao.com/v2/local/search/address.json?query=" + searchMap;

        return restTemplate.exchange(rawURI, HttpMethod.GET, entity, String.class);
    }
}
