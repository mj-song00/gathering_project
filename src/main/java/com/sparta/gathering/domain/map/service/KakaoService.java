package com.sparta.gathering.domain.map.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.map.dto.request.MapRequest;
import com.sparta.gathering.domain.map.dto.response.AroundPlaceResponse;
import com.sparta.gathering.domain.map.entity.Map;
import com.sparta.gathering.domain.map.repository.MapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class KakaoService {

    private final RestTemplate restTemplate;
    private final HttpHeaders headers = new HttpHeaders();
    private final MapRepository mapRepository;
    private final ObjectMapper objectMapper;
    private final GatherRepository gatherRepository;


    @Value("${kakao.map.api-key}")
    private String appKey;

    public ResponseEntity<String> searchMap(MapRequest searchMap) {

        // Set up headers
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("Authorization", appKey);

        // Create entity with headers
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        String rawURI = "https://dapi.kakao.com/v2/local/search/address.json?query=" + searchMap.getAddress();

        return restTemplate.exchange(rawURI, HttpMethod.GET, entity, String.class);
    }


    @Transactional
    public void saveMap(MapRequest saveMap, Long gatherId) {

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", appKey);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        String rawURI = "https://dapi.kakao.com/v2/local/search/address.json?query=" + saveMap.getAddress();
        ResponseEntity<String> response = restTemplate.exchange(rawURI, HttpMethod.GET, entity, String.class);
        Gather gather = gatherRepository.findById(gatherId).orElseThrow(() ->
                new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
        try {
            JsonNode mapData = objectMapper.readTree(response.getBody()); //String 을 json형태로 변환

            //json에서 주소와 위경도를 뽑음
            String addressName = mapData.get("documents").get(0).get("address_name").asText();
            Double latitude = mapData.get("documents").get(0).get("y").asDouble();
            Double longitude = mapData.get("documents").get(0).get("x").asDouble();

            //entity에 저장
            Map map = new Map(addressName, latitude, longitude);
            mapRepository.save(map);

        } catch (JsonMappingException e) {
            throw new BaseException(ExceptionEnum.PERMISSION_DENIED_ROLE);
        } catch (JsonProcessingException e) {
            throw new BaseException(ExceptionEnum.JSON_TYPE_MISMATCH);
        }
    }


    @Transactional
    public List<AroundPlaceResponse> listMyMap(Double x, Double y) {//경도 x lon,위도 y lat
        List<Map> maps = mapRepository.findWithinBounds(x, y);

        return maps.stream()
                .map(map -> new AroundPlaceResponse(map.getAddressName(), map.getLatitude(), map.getLongitude()))
                .collect(Collectors.toList());
    }
}
