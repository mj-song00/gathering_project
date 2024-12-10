package com.sparta.gathering.domain.gather;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.common.service.SlackNotifierService;
import com.sparta.gathering.domain.category.entity.Category;
import com.sparta.gathering.domain.category.repository.CategoryRepository;
import com.sparta.gathering.domain.gather.dto.request.GatherRequest;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.gather.service.GatherServiceImpl;
import com.sparta.gathering.domain.map.entity.Map;
import com.sparta.gathering.domain.map.repository.MapRepository;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import com.sparta.gathering.domain.user.service.factory.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GatherServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GatherRepository gatherRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MapRepository mapRepository;

//    @Mock
//    private ElasticRepository elasticRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @Mock
    private SlackNotifierService slackNotifierService;

    @InjectMocks
    private GatherServiceImpl gatherService;

    private Category category;
    private User adminUser;
    private User user;
    private Gather gather;
    private AuthenticatedUser authenticatedUser;
    private GatherRequest request;
    private GatherRequest newRequest;
    private Map map;
    private User manager;
    private Member member;
    private Long gatherId = 1L;
    private Pageable pageable;
    private List<String> hashTagName;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id(UUID.randomUUID())
                .email("admin@test.com")
                .password("password123A!")
                .nickName("nickname")
                .userRole(UserRole.ROLE_ADMIN)
                .identityProvider(IdentityProvider.NONE)
                .profileImage(null)
                .build();

        user = UserFactory.of(
                "test@test.com",
                "Nickname",
                "Asdf1234!",
                UserRole.ROLE_USER,
                IdentityProvider.NONE,
                null
        );


        manager = User.builder()
                .id(UUID.randomUUID())
                .email("manager@example.com")
                .password("password123A!")
                .nickName("managerUser")
                .userRole(UserRole.ROLE_USER)
                .identityProvider(IdentityProvider.NONE)
                .profileImage(null)
                .build();

        authenticatedUser = new AuthenticatedUser(user.getId(), user.getEmail(), null);

        category = new Category("카테고리 제목", adminUser);
        request = new GatherRequest("모임 제목", "모임 설명", List.of("Tag1", "Tag2"), "서울시 동작구", 37.5665, 126.9780);
        gather = new Gather("모임 제목", "모임 내용", category, List.of("Tag1", "Tag2"));
        gatherId = gather.getId();
        member = new Member(manager, gather, Permission.MANAGER);
        newRequest = new GatherRequest("New Title", "New Description",List.of("newHashtag1", "newHashtag2"),"서울시 동작구", 37.5665,126.9780);
        map = Map.builder()
                .id(1L)
                .addressName("test")
                .latitude(100.0)
                .longitude(200.0)
                .gather(gather)
                .build();

        // Map 초기화 및 Gather와의 연관관계 설정
        map = new Map();
        map.setGather(gather); // 양방향 관계 설정
        map.setAddressName("Sample Address");

        // Gather에 Map 설정
        gather.setMap(map); // Gather와 Map 연결
    }

    @Nested
    @DisplayName("모임 생성")
    class Create {
        @Test
        @DisplayName("카테고리 조회 성공 테스트")
        void testGetCategorySuccess() {
            // given
            Long categoryId = category.getId();
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

            // when
            Category result = gatherService.getCategory(categoryId);

            // then
            assertNotNull(result);
            assertEquals(category.getId(), result.getId());
        }

        @Test
        @DisplayName("카테고리 조회 실패 테스트")
        void testGetCategoryNotFound() {
            // given
            Long categoryId = 999L;  // 존재하지 않는 카테고리 ID
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(BaseException.class, () -> gatherService.getCategory(categoryId));
        }

        @Test
        @DisplayName("사용자 조회 성공 테스트")
        void testGetUserSuccess() {
            // given
            UUID userId = authenticatedUser.getUserId();
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            // when
            User result = gatherService.getUser(userId);

            // then
            assertNotNull(result);
            assertEquals(user.getId(), result.getId());
        }

        @Test
        @DisplayName("사용자 조회 실패 테스트")
        void testGetUserNotFound() {
            // given
            UUID userId = UUID.randomUUID();  // 존재하지 않는 사용자 ID
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(BaseException.class, () -> gatherService.getUser(userId));
        }

//        @Test
//        @DisplayName("Gather 및 Member 저장 성공 테스트")
//        void testSaveGatherAndMember() {
//            // given
//            Map newMap = new Map(request.getAddressName(), request.getLatitude(), request.getLongitude());
//            Gather gather = new Gather(request.getTitle(), request.getDescription(), category, request.getHashtags());
//            gather.saveMap(newMap);
//            Member member = new Member(user, gather, Permission.MANAGER);
//            GatherDocument document= new GatherDocument(gather.getId().toString() ,gather.getTitle(),gather.getCategory().getCategoryName(),gather.getDescription(),gather.getMap().getAddressName(),gather.getGatherHashtags());
//             when
//            gatherService.saveData(gather, member,document);
//
//             then
//            verify(gatherRepository).save(gather);
//            verify(memberRepository).save(member);
//            verify(elasticRepository).save(document);
//        }

        @Test
        @DisplayName("Slack 알림 전송 테스트")
        void testSendSlackNotification() {
            // given
            Map newMap = new Map(request.getAddressName(), request.getLatitude(), request.getLongitude());
            Gather gather = new Gather(request.getTitle(), request.getDescription(), category, request.getHashtags());
            gather.saveMap(newMap);

            // when
            gatherService.sendSlackNotification(category, request);

            // then
            verify(slackNotifierService).sendNotification(contains("[모임이 생성되었습니다]"));
        }
    }


    @Nested
//    @DisplayName("모임 수정")
    class Modify {
//        @Test
//        @DisplayName("수정실패 - 존재하지 않는 모임")
//        void failModifyGatherNotFound() {
//            when(gatherRepository.findById(gatherId)).thenReturn(Optional.empty());
//            BaseException exception = assertThrows(BaseException.class, () -> {
//                gatherService.modifyGather(request, gatherId, authenticatedUser);
//            });
//
//            assertEquals(ExceptionEnum.GATHER_NOT_FOUND, exception.getExceptionEnum());
//        }

        @Test
        @DisplayName("수정 실패 - 매니저 권한 없음")
        void failModifyManagerNotFound() {
            when(gatherRepository.findById(gatherId)).thenReturn(Optional.of(gather));

            BaseException exception = assertThrows(BaseException.class, () -> {
                gatherService.modifyGather(request, gatherId, authenticatedUser);
            });
            assertEquals(ExceptionEnum.MANAGER_NOT_FOUND, exception.getExceptionEnum());
        }


        @Test
        @DisplayName("수정 실패 - 지도 정보 없음")
        void failModifyMapNotFound() {
            when(gatherRepository.findById(gatherId)).thenReturn(Optional.empty());

            BaseException exception = assertThrows(BaseException.class, () -> {
                gatherService.modifyGather(request, gatherId, authenticatedUser);
            });

            assertEquals(ExceptionEnum.GATHER_NOT_FOUND, exception.getExceptionEnum());
        }

        @Test
        @DisplayName("수정 성공 - redis 제외")
        void successModify() {
            // Given: Mock 데이터와 초기 상태 설정
            gatherId = 1L;

            // Redis 관련 동작을 Mock 처리
            when(redisTemplate.opsForZSet()).thenReturn(zSetOperations); // ZSetOperations Mock 리턴
            doReturn(0.0).when(zSetOperations).incrementScore(anyString(), anyString(), anyDouble()); // incrementScore Mock 처리

            when(gatherRepository.findById(gatherId)).thenReturn(Optional.of(gather));
            when(memberRepository.findManagerIdByGatherId(gatherId)).thenReturn(Optional.of(UUID.randomUUID()));  // Mocking the method being called
            when(mapRepository.findByGatherId(gatherId)).thenReturn(Optional.of(map));

            AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                    member.getUser().getId(),
                    "manager@example.com",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")) // 권한 설정
            );

            // When: 서비스 메서드 호출
            gatherService.modifyGather(newRequest, gatherId, authenticatedUser);

            // Then: 결과 검증
            verify(gatherRepository).findById(gatherId); // 모임 조회 확인
            verify(mapRepository).findByGatherId(gatherId); // 지도 조회 확인
            verify(memberRepository).findManagerIdByGatherId(gatherId); // 올바른 메서드 확인
            verify(gatherRepository).save(gather); // 저장 확인

            // Gather 객체 업데이트 내용 검증
            assertEquals("New Title", gather.getTitle());
            assertEquals("New Description", gather.getDescription());
//            List<String> actualHashtags = gather.getHashTagList().stream()
//                    .map(hashTag -> hashTag.getHashTagName())
//                    .filter(name -> !name.equals("Tag1") && !name.equals("Tag2")) // 기존 해시태그 제외
//                    .collect(Collectors.toList());

            List<String> expectedHashtags = List.of("newHashtag1", "newHashtag2");

       //     assertEquals(expectedHashtags, actualHashtags);
            assertEquals(map, gather.getMap());
        }

        @Nested
        @DisplayName("모임 삭제")
        class Delete {
            @Test
            @DisplayName("삭제실패 - 존재하지 않는 모임")
            void failDeleteGatherNotFound() {
                when(gatherRepository.findById(gatherId)).thenReturn(Optional.empty());
                BaseException exception = assertThrows(BaseException.class, () -> {
                    gatherService.deleteGather(gatherId, authenticatedUser);
                });

                assertEquals(ExceptionEnum.GATHER_NOT_FOUND, exception.getExceptionEnum());
            }

            @Test
            @DisplayName("삭제 실패 - 매니저 권한 없음")
            void failModifyManagerNotFound() {
                when(gatherRepository.findById(gatherId)).thenReturn(Optional.of(gather));

                BaseException exception = assertThrows(BaseException.class, () -> {
                    gatherService.deleteGather(gatherId, authenticatedUser);
                });
                assertEquals(ExceptionEnum.MANAGER_NOT_FOUND, exception.getExceptionEnum());
            }

            @Test
            @DisplayName("삭제 성공 - redis 제외")
            void successDelete() {
                // Given: Mock 데이터와 초기 상태 설정
                gatherId = 1L;

                // doNothing().when(gather).delete();
                // Redis 관련 동작을 Mock 처리
                when(redisTemplate.opsForZSet()).thenReturn(zSetOperations); // ZSetOperations Mock 리턴
                doReturn(0.0).when(zSetOperations).incrementScore(anyString(), anyString(), anyDouble()); // incrementScore Mock 처리

                when(gatherRepository.findById(gatherId)).thenReturn(Optional.of(gather));
                when(memberRepository.findManagerIdByGatherId(gatherId)).thenReturn(Optional.of(UUID.randomUUID()));  // Mocking the method being called

                AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                        member.getUser().getId(),
                        "manager@example.com",
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")) // 권한 설정
                );

                gatherService.deleteGather(gatherId, authenticatedUser);

                assertNotNull(gather.getDeletedAt());

                // repository.save()가 호출되었는지 확인
                verify(gatherRepository).save(gather);
            }
        }

        @Nested
        @DisplayName("모임 검식")
        class Search {
            @Test
            @DisplayName("해시테그 검색")
            void successSearchHashtags() {
                pageable = PageRequest.of(0, 10);  // 0번째 페이지, 한 페이지당 10개의 데이터
                hashTagName = List.of("Tag1", "Tag2");

                // mock된 Gather 객체 생성
                Gather gather1 = new Gather("모임1", "내용1", new Category(), List.of("Tag1", "Tag3"));
                Gather gather2 = new Gather("모임2", "내용2", new Category(), List.of("Tag2", "Tag1"));
                Page<Gather> gatherPage = new PageImpl<>(List.of(gather1, gather2));  // mock된 결과 페이지

                // gatherRepository.findByKeywords가 mock된 결과를 반환하도록 설정
                when(gatherRepository.findByKeywords(pageable, hashTagName)).thenReturn(gatherPage);


                pageable = PageRequest.of(0, 10);  // 0번째 페이지, 한 페이지당 10개의 데이터
                Page<Gather> result = gatherService.findByHashTags(pageable, hashTagName);

                // Then: 결과 검증
                assertNotNull(result);  // 결과가 null이 아님을 검증
                assertEquals(2, result.getContent().size());  // 결과 페이지에 2개의 Gather가 있어야 함
//                assertTrue(result.getContent().stream().anyMatch(gather -> gather.getHashTagList().stream()
//                        .anyMatch(hashTag -> hashTag.getHashTagName().equals("Tag1"))));
//
//                assertTrue(result.getContent().stream().anyMatch(gather -> gather.getHashTagList().stream()
//                        .anyMatch(hashTag -> hashTag.getHashTagName().equals("Tag2"))));

                // verify: gatherRepository.findByKeywords가 정확히 호출되었는지 확
                verify(gatherRepository).findByKeywords(pageable, hashTagName);
            }

            @Test
            @DisplayName("해시테그 검색- no result")
            void searchNoResult() {
                List<String> emptyHashTagName = List.of();
                Page<Gather> emptyPage = Page.empty();
                when(gatherRepository.findByKeywords(pageable, emptyHashTagName)).thenReturn(emptyPage);

                // When: findByHashTags 메서드 호출
                Page<Gather> result = gatherService.findByHashTags(pageable, emptyHashTagName);

                // Then: 결과가 비어 있어야 함
                assertNotNull(result);  // 결과가 null이 아님을 검증
                assertEquals(0, result.getContent().size());  // 결과가 빈 페이지여야 함
            }

            @Test
            @DisplayName("카테고리 검색 - 성공")
            void successSearchCategory() {
                // Given: Mock 데이터와 초기 상태 설정
                Long categoryId = 1L;
                Pageable pageable = PageRequest.of(0, 10); // 페이지 0, 페이지당 10개 데이터

                // 카테고리 설정 및 gather 객체 생성
                Category category = new Category("운동", adminUser); // adminUser는 적절히 설정된 사용자
                Gather gather1 = new Gather("모임1", "내용1", category, List.of("Tag1", "Tag2"));
                Gather gather2 = new Gather("모임2", "내용2", category, List.of("Tag2", "Tag3"));

                // 결과로 반환할 Page 객체
                Page<Gather> gatherPage = new PageImpl<>(List.of(gather1, gather2));  // mock된 결과 페이지

                // gatherRepository.findByCategoryWithHashTags() 메서드 Mock 설정
                when(gatherRepository.findByCategoryWithHashTags(pageable, categoryId)).thenReturn(gatherPage);

                // When: gatherService.gathers() 호출
                Page<Gather> result = gatherService.gathers(pageable, categoryId);

                // Then: 결과 검증
                assertNotNull(result);  // 결과가 null이 아님을 검증
                assertEquals(2, result.getContent().size());  // 결과 페이지에 2개의 Gather가 있어야 함
                assertTrue(result.getContent().stream().anyMatch(gather -> gather.getCategory().getCategoryName().equals("운동")));

                // verify: gatherRepository.findByCategoryWithHashTags가 정확히 호출되었는지 확인
                verify(gatherRepository).findByCategoryWithHashTags(pageable, categoryId);
            }

            @Test
            @DisplayName("카테고리 검색 - no result")
            void searchCategoryNoResult() {
                Long categoryId = 1L;
                Page<Gather> emptyPage = Page.empty();
                when(gatherRepository.findByCategoryWithHashTags(pageable, categoryId)).thenReturn(emptyPage);

                Page<Gather> result = gatherService.gathers(pageable, categoryId);

                assertNotNull(result);
                assertEquals(0, result.getContent().size());

                verify(gatherRepository).findByCategoryWithHashTags(pageable, categoryId);
            }

//            @Test
//            @DisplayName("title 검색")
//            void successSearchTitle() {
//                Pageable pageable = PageRequest.of(0, 10);
//                Gather gather1 = new Gather("모임1", "내용1", category, List.of("Tag1", "Tag2"));
//                Gather gather2 = new Gather("모임2", "내용2", category, List.of("Tag2", "Tag3"));
//                Page<Gather> gatherPage = new PageImpl<>(List.of(gather1, gather2));
//                when(gatherRepository.findByTitle(pageable, gather.getTitle())).thenReturn(gatherPage);
//
//                Page<Gather> result = gatherService.findByTitles(pageable, gather.getTitle());
//                assertNotNull(result);
//                assertEquals(2, result.getContent().size());
//
//                assertTrue(result.getContent().stream().anyMatch(gather -> gather.getTitle().equals("모임1")));
//                assertTrue(result.getContent().stream().anyMatch(gather -> gather.getTitle().equals("모임2")));
//
//            }

//            @Test
//            @DisplayName("title 검색 - no result")
//            void searchTitleNoResult() {
//                Page<Gather> emptyPage = Page.empty();
//                Gather gather1 = new Gather("모임1", "내용1", category, List.of("Tag1", "Tag2"));
//                Gather gather2 = new Gather("모임2", "내용2", category, List.of("Tag2", "Tag3"));
//                Page<Gather> gatherPage = new PageImpl<>(List.of(gather1, gather2));
//                when(gatherRepository.findByTitle(pageable, gather.getTitle())).thenReturn(emptyPage);
//
//                Page<Gather> result = gatherService.findByTitles(pageable, gather.getTitle());
//                assertNotNull(result);
//                assertEquals(0, result.getContent().size());
//
//                verify(gatherRepository).findByTitle(pageable, gather.getTitle());
//            }
        }
    }
}
