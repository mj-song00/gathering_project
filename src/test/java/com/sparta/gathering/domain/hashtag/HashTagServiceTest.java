/*
package com.sparta.gathering.domain.hashtag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.category.entity.Category;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.hashtag.dto.request.HashTagsReq;
import com.sparta.gathering.domain.hashtag.entity.HashTag;
import com.sparta.gathering.domain.hashtag.repository.HashTagRepository;
import com.sparta.gathering.domain.hashtag.service.HashTagService;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.UserRole;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HashTagServiceTest {

    @Mock
    private HashTagRepository hashTagRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GatherRepository gatherRepository;

    @InjectMocks
    private HashTagService hashTagService;

    private AuthenticatedUser authenticatedUser;
    private Gather gather;
    private Member member;
    private HashTagsReq hashTagsReq;
    private User testUser;
    private Category category;

    @BeforeEach
    void setUp() {
        authenticatedUser = new AuthenticatedUser(UUID.randomUUID(), "test@example.com", null);
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .password("password123A!")
                .nickName("nickname")
                .userRole(UserRole.ROLE_ADMIN)
                .identityProvider(IdentityProvider.NONE)
                .profileImage(null)
                .build();
        category = new Category("카테고리 제목", testUser);
        hashTagsReq = new HashTagsReq(List.of("Tag1", "Tag2"));
        List<String> hashTags = new ArrayList<>(List.of("Tag1", "Tag2"));
        gather = new Gather("모임 제목", "모임 내용", category, hashTags);
        member = new Member(testUser, gather, Permission.MANAGER);
    }

//    @Test
//    @DisplayName("해시태그 생성 성공")
//    void test1() {
//        // given
//        when(memberRepository.findByUserId(authenticatedUser.getUserId())).thenReturn(Optional.of(member));
//        when(gatherRepository.findById(gather.getId())).thenReturn(Optional.of(gather));
////        when(hashTagRepository.findByGatherIdAndHashTagNameIn(gather.getId(), hashTagsReq.getHashTagName())).thenReturn(List.of());
//        when(hashTagRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // when
//        List<HashTagRes> result = hashTagService.createHashTag(authenticatedUser, gather, hashTagsReq);
//
//        // then
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        verify(hashTagRepository, times(1)).saveAll(anyList());
//    }

//    @Test
//    @DisplayName("해시태그 생성 실패 - 중복된 해시태그")
//    void test2() {
//        // given
//        when(memberRepository.findByUserId(authenticatedUser.getUserId())).thenReturn(Optional.of(member));
//        when(gatherRepository.findById(gather.getId())).thenReturn(Optional.of(gather));
//   //     when(hashTagRepository.findByGatherIdAndHashTagNameIn(gather.getId(), hashTagsReq.getHashTagName())).thenReturn(List.of(new HashTag()));
//
//        // when & then
//        BaseException exception = assertThrows(BaseException.class, () ->
//                hashTagService.createHashTag(authenticatedUser, gather, hashTagsReq));
//
//        assertEquals(ExceptionEnum.ALREADY_HAVE_HASHTAG, exception.getExceptionEnum());
//        verify(hashTagRepository, never()).saveAll(anyList());
//    }

@Test
    @DisplayName("해시태그 삭제 성공")
    void test3() {
        // given
        Long hashTagId = 1L;
        HashTag hashTag = HashTag.of("TestTag", gather);

        when(memberRepository.findByUserId(authenticatedUser.getUserId())).thenReturn(Optional.of(member));
        when(gatherRepository.findById(gather.getId())).thenReturn(Optional.of(gather));
        when(hashTagRepository.findById(hashTagId)).thenReturn(Optional.of(hashTag));

        // when
        hashTagService.deleteHashTag(authenticatedUser, gather, hashTagId);

        // then
        assertNotNull(hashTag.getDeletedAt());
        verify(hashTagRepository, times(1)).findById(hashTagId);
    }


    @Test
    @DisplayName("해시태그 조회 성공")
    void test4() {
        // given
//        when(gatherRepository.findById(gather.getId())).thenReturn(Optional.of(gather));
//        when(hashTagRepository.findByGatherIdAndDeletedAtIsNull(gather.getId())).thenReturn(List.of(
//                HashTag.of("Tag1", gather),
//                HashTag.of("Tag2", gather)
//        ));
//
//        // when
//        List<HashTagRes> result = hashTagService.getHashTagList(gather);
//
//        // then
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        verify(hashTagRepository, times(1)).findByGatherIdAndDeletedAtIsNull(gather.getId());
    }


@Test
    @DisplayName("유효하지 않은 멤버로 예외 발생")
    void test5() {
        // Arrange
        when(memberRepository.findByUserId(authenticatedUser.getUserId())).thenReturn(Optional.empty());
        when(gatherRepository.findById(gather.getId())).thenReturn(Optional.of(gather));

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                hashTagService.createHashTag(authenticatedUser, gather, hashTagsReq));

        assertEquals(ExceptionEnum.USER_NOT_FOUND, exception.getExceptionEnum());
    }


 @Test
    @DisplayName("유효하지 않은 모임로 예외 발생")
    void test6() {
        // given
        when(gatherRepository.findById(gather.getId())).thenReturn(Optional.empty());
        // when
        BaseException exception = assertThrows(BaseException.class, () ->
                hashTagService.createHashTag(authenticatedUser, gather, hashTagsReq)
        );
        // then
        assertEquals(ExceptionEnum.GATHER_NOT_FOUND, exception.getExceptionEnum());
        verify(hashTagRepository, never()).save(any(HashTag.class));
    }


}
*/
