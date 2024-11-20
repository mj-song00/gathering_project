package com.sparta.gathering.domain.gather;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.domain.category.entity.Category;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.gather.service.GatherServiceImpl;
import com.sparta.gathering.domain.map.entity.Map;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class GatherServiceRedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @Autowired
    private GatherServiceImpl gatherService;


    @MockBean
    private GatherRepository gatherRepository;

    private static final String REDIS_CITY_KEY = "city";
    private Category category;
    private Gather gather;
    private Member member;
    private User manager;

    @BeforeEach
    void setUp() {

        manager = User.builder().id(UUID.randomUUID()).email("manager@example.com").password("password123A!").nickName("managerUser").userRole(UserRole.ROLE_USER).identityProvider(IdentityProvider.NONE).profileImage(null).build();

        // Redis 초기화
        //redisTemplate.getConnectionFactory().getConnection().flushAll();
        gather = new Gather("모임 제목", "모임 내용", category, List.of("Tag1", "Tag2"));
        member = new Member(manager, gather, Permission.MANAGER);
    }

    @Nested
    @DisplayName("redis test")
    class Create {
        @Test
        @DisplayName("Redis score 관련 테스트")
        void scoreAddForNewGatherTest() {
            // Given: Redis에 해당 값이 없는 경우
            String addressName = "서울시 동작구";
            Map map = new Map(addressName, 37.5665, 126.9780);
            Gather gather = new Gather("모임 제목", "모임 설명", null, List.of("Tag1"));
            gather.saveMap(map);

            // When
            gatherService.updateRedisZSet(gather);

            // Then
            Double score = redisTemplate.opsForZSet().score("city", addressName);
            assertThat(score).isEqualTo(1.0); // 새 값이 추가됨
        }

        @Test
        @DisplayName("Redis - score +1")
        void incrementScoreTest() {
            // Given: Redis에 초기 데이터를 설정
            String addressName = "서울시 동작구";
            redisTemplate.opsForZSet().add("city", addressName, 1.0);

            Map map = new Map(addressName, 37.5665, 126.9780);
            Gather gather = new Gather("모임 제목", "모임 설명", null, List.of("Tag1"));
            gather.saveMap(map);

            // When: Redis ZSet 갱신
            gatherService.updateRedisZSet(gather);

            // Then: 결과 검증
            Double score = redisTemplate.opsForZSet().score("city", addressName);
            assertThat(score).isEqualTo(2.0); // 기존 값에서 +1
        }

        @Test
        @DisplayName("redis score -1")
        void decreaseScoreTest() {
            // Given: 테스트용 데이터 준비
            Long gatherId = 1L;
            String addressName = "서울시 동작구";

            // Gather 객체 준비
            Gather gather = new Gather("모임 제목", "모임 설명", null, List.of("Tag1"));
            Map map = new Map(addressName, 37.5665, 126.9780);
            gather.saveMap(map);


            // 인증된 사용자 (AuthenticatedUser) 객체 생성
            AuthenticatedUser authenticatedUser = new AuthenticatedUser(member.getUser().getId(), "manager@example.com", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")) // 권한 설정
            );

            // Mocking: gather 조회 Mocking
            Mockito.when(gatherRepository.findById(gatherId)).thenReturn(Optional.of(gather));

            // When: Gather 삭제 메소드 호출
            gatherService.deleteGather(gatherId, authenticatedUser);

            // Then: Redis ZSet에서 점수가 0로 감소했는지 확인
            Mockito.verify(zSetOperations, times(0)).incrementScore(REDIS_CITY_KEY, addressName, -1.0);

            // And: Gather 삭제 후 저장 확인
            Mockito.verify(gatherRepository, times(1)).save(gather);
        }

    }
}
