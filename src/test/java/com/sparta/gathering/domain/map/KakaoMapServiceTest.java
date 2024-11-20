package com.sparta.gathering.domain.map;

import com.sparta.gathering.domain.map.dto.request.MapRequest;
import com.sparta.gathering.domain.map.dto.response.AroundPlaceResponse;
import com.sparta.gathering.domain.map.service.KakaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class KakaoMapServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KakaoService kakaoService;

    private HttpHeaders headers;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private GeoOperations<String, String> geoOperations;


    @BeforeEach
    void setUp() {
        // This ensures Mockito initializes the mocks before each test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void searchMap_ShouldReturnExpectedResponse() {
        // Arrange
        MapRequest mapRequest = new MapRequest("Seoul");
        String mockResponse = "{\"documents\": [{\"address_name\": \"Seoul, South Korea\"}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        // Mock the restTemplate's behavior
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        ResponseEntity<String> response = kakaoService.searchMap(mapRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }
//
//    // Test for nearByVenues method
//    @Test
//    public void nearByVenues_ShouldReturnExpectedVenues() {
//        // Arrange
//        Double longitude = 127.0;
//        Double latitude = 37.5;
//        Integer distance = 3;
//
//        // Mock the geoOperations calls
//        when(redisTemplate.opsForGeo()).thenReturn(geoOperations);
//        when(geoOperations.position("map", "Venue1")).thenReturn(Collections.singletonList(new Point(127.0, 37.5)));
//        when(geoOperations.position("map", "Venue2")).thenReturn(Collections.singletonList(new Point(127.1, 37.6)));
//
//        // Act
//        List<AroundPlaceResponse> result = kakaoService.nearByVenues(longitude, latitude, distance);
//
//        // Debugging step: print the result to see the actual data
//        result.forEach(venue -> {
//            System.out.println("Venue: " + venue.getAddress() + ", Longitude: " + venue.getLongitude() + ", Latitude: " + venue.getLatitude());
//        });
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(2, result.size()); // Expecting two venues
//
//        // Check the properties of the first venue
//        AroundPlaceResponse venue1 = result.get(0);
//        assertEquals("Venue1", venue1.getAddress());  // Ensure correct getter method
//        assertEquals(127.0, venue1.getLongitude());
//        assertEquals(37.5, venue1.getLatitude());
//
//        // Check the properties of the second venue
//        AroundPlaceResponse venue2 = result.get(1);
//        assertEquals("Venue2", venue2.getAddress());  // Ensure correct getter method
//        assertEquals(127.1, venue2.getLongitude());
//        assertEquals(37.6, venue2.getLatitude());
//    }
}
