package com.sparta.gathering.domain.map.controller;

//import com.sparta.gathering.domain.map.service.KakaoService;
import com.sparta.gathering.domain.map.service.KakaoService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.parser.ParseException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@RestController
public class KakaoMapController {

    private final RestTemplate restTemplate;
    private final KakaoService kakaoService;
//    private final KakaoService kakaoService;

    /**
     * 카카오맵 지번으로 검색
     * @param searchMap 검색할 주소
     */
    @GetMapping("/api/kakaoMap/{searchMap}")
    public ResponseEntity<String> searchMap(@PathVariable String searchMap) {

        return kakaoService.searchMap(searchMap);
    }

    @PostMapping("/api/kakaoMap/{saveMap}")
    public void saveMap(@PathVariable String saveMap) {
        kakaoService.saveMap(saveMap);
    }
}
