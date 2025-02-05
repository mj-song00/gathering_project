package com.sparta.gathering.domain.map.service;

import com.sparta.gathering.domain.map.dto.request.MapRequest;
import com.sparta.gathering.domain.map.dto.response.AroundPlaceResponse;
import com.sparta.gathering.domain.map.entity.Map;
import com.sparta.gathering.domain.map.repository.MapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class KakaoService {

    private final RestTemplate restTemplate;
    private final HttpHeaders headers = new HttpHeaders();
    private final MapRepository mapRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final GeoOperations<String, String> geoOperations;

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
    public List<AroundPlaceResponse> listMyMap(Double x, Double y, Integer d) {//경도 x lon,위도 y lat
        List<Map> maps = mapRepository.findWithinBounds(x, y, d);

        return maps.stream()
                .map(map -> new AroundPlaceResponse(map.getAddressName(), map.getLatitude(), map.getLongitude()))
                .collect(Collectors.toList());
    }


    public List<AroundPlaceResponse> nearByVenues(Double longitude, Double latitude, Integer distance) {

        Point searchPoint = new Point(longitude, latitude);
        Distance radius = new Distance(distance, Metrics.KILOMETERS); // search within 3 km
        //래디스에서 위치 기반으로 데이터 가져오기
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOperations.radius("map", new Circle(searchPoint, radius));

        // redis geo 자료구조 설정
        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();

        assert results != null;

        // List 형태로 변환
        return results.getContent().stream()
                .flatMap(c -> {
                    List<Point> position = geoOperations.position("map", c.getContent().getName());
                    assert position != null;

                    return position.stream().map(point -> new AroundPlaceResponse(c.getContent().getName(), point.getX(), point.getY()));
                })
                .collect(Collectors.toList());
    }
}
