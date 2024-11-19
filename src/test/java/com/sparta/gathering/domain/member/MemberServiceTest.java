package com.sparta.gathering.domain.member;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.category.entity.Category;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import com.sparta.gathering.domain.member.service.MemberServiceImpl;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GatherRepository gatherRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private AuthenticatedUser authenticatedUser;
    private Gather gather;
    private Member member;
    private User testUser;
    private User managerUser;
    private Category category;
    private Pageable pageable;


    @BeforeEach
    void setUp() {
        authenticatedUser = new AuthenticatedUser(UUID.randomUUID(), "test@example.com", null);

        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .password("password123A!")
                .nickName("nickname")
                .userRole(UserRole.ROLE_USER)
                .identityProvider(IdentityProvider.NONE)
                .profileImage(null)
                .build();

        managerUser = User.builder()
                .id(UUID.randomUUID())
                .email("manager@example.com")
                .password("password123A!")
                .nickName("managerUser")
                .userRole(UserRole.ROLE_USER)
                .identityProvider(IdentityProvider.NONE)
                .profileImage(null)
                .build();

        category = new Category("카테고리 제목", testUser);
        gather = new Gather("모임 제목", "모임 내용", category, List.of("Tag1", "Tag2"));
        gather.setId(1L);
        member = new Member(managerUser, gather, Permission.MANAGER);
        member.setId(1L);
        pageable = PageRequest.of(0, 10);

    }

    @Nested
    @DisplayName("모임가입 신청 관련")
    class apply{
        @Test
        @DisplayName("모임 가입 신청 성공")
        void testApplyForGather() {
            // given
            UUID userId = testUser.getId();
            long gatherId = gather.getId();

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(gatherRepository.findById(gatherId)).thenReturn(Optional.of(gather));
            when(memberRepository.findByGatherIdAndPermission(gatherId, Permission.MANAGER))
                    .thenReturn(Optional.of(member)); // 기존 매니저 존재

            // when
            memberService.createMember(userId, gatherId);

            // then
            verify(userRepository).findById(userId);
            verify(gatherRepository).findById(gatherId);
            verify(memberRepository).findByGatherIdAndPermission(gatherId, Permission.MANAGER);
            verify(memberRepository).save(argThat(member -> member.getPermission() == Permission.PENDDING));
        }

        @Test
        @DisplayName("멤버 신청 실패 - 본인이 매니저")
        void registration_failed(){
            UUID userId = managerUser.getId(); // 매니저 ID
            long gatherId = gather.getId();


            when(userRepository.findById(userId)).thenReturn(Optional.of(managerUser));
            when(gatherRepository.findById(gatherId)).thenReturn(Optional.of(gather));
            when(memberRepository.findByGatherIdAndPermission(gatherId, Permission.MANAGER))
                    .thenReturn(Optional.of(member)); // 기존 매니저 존재

            // when & then
            BaseException exception = assertThrows(BaseException.class, () -> {
                memberService.createMember(userId, gatherId);
            });

            assertEquals(ExceptionEnum.MEMBER_NOT_ALLOWED, exception.getExceptionEnum()); // 본인이 매니저일 경우 예외 발생
        }

        @Test
        @DisplayName("유저가 없으면 예외 발생")
        void user_not_found() {
            // given
            UUID userId = UUID.randomUUID(); // 존재하지 않는 유저 ID
            long gatherId = gather.getId();

            when(userRepository.findById(userId)).thenReturn(Optional.empty()); // 유저가 존재하지 않음

            // when & then
            BaseException exception = assertThrows(BaseException.class, () -> {
                memberService.createMember(userId, gatherId);
            });

            assertEquals(ExceptionEnum.USER_NOT_FOUND, exception.getExceptionEnum()); // 유저가 없을 경우 예외 발생
        }

        @Test
        @DisplayName("모임이 없으면 예외 발생")
        void gather_not_found() {
            // given
            UUID userId = testUser.getId();
            long gatherId = 999L; // 존재하지 않는 모임 ID

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(gatherRepository.findById(gatherId)).thenReturn(Optional.empty()); // 모임이 존재하지 않음

            // when & then
            BaseException exception = assertThrows(BaseException.class, () -> {
                memberService.createMember(userId, gatherId);
            });

            assertEquals(ExceptionEnum.GATHER_NOT_FOUND, exception.getExceptionEnum()); // 모임이 없을 경우 예외 발생
        }
    }

    @Nested
    @DisplayName("모임조회 관련")
    class check{
        @Test
        @DisplayName("모임 조회 성공 - 페이징 반환")
        void success_check_gather(){
            //given
            List<Member> members = List.of(member);
            Page<Member> memberPage = new PageImpl<>(members, pageable, members.size());

            Mockito.when(memberRepository.findByGatherIdAndDeletedAtIsNull(pageable, gather.getId()))
                    .thenReturn(memberPage);

            // When
            Page<Member> result = memberService.getMembers(pageable, gather.getId());

            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(managerUser.getId(), result.getContent().get(0).getUser().getId());
            assertEquals(Permission.MANAGER, result.getContent().get(0).getPermission());

            Mockito.verify(memberRepository, Mockito.times(1))
                    .findByGatherIdAndDeletedAtIsNull(pageable, gather.getId());

        }
    }

    @Nested
    @DisplayName("모임승인 관련")
    class approval{
        @Test
        @DisplayName("가입 승인")
        void success_approval(){
            // given
            long memberId = member.getId();
            long gatherId = gather.getId();

            // 매니저 ID를 반환하도록 설정
            when(memberRepository.findManagerIdByGatherId(gatherId)).thenReturn(Optional.of(authenticatedUser.getUserId()));
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

            // when
            memberService.approval(memberId, gatherId, authenticatedUser);

            // then
            assertEquals(Permission.GUEST, member.getPermission());
            verify(memberRepository).findById(memberId);
            verify(memberRepository).save(member);
        }

        @Test
        @DisplayName("승인 실패 - 매니저 계정 아님 ")
        void fail_approval(){
            // given
            long memberId = member.getId();
            long gatherId = gather.getId();
            AuthenticatedUser nonManagerUser = new AuthenticatedUser(UUID.randomUUID(), "nonmanager@example.com", null);

            // memberRepository에서 findManagerIdByGatherId 호출 시 Optional.empty()를 반환하도록 설정
            when(memberRepository.findManagerIdByGatherId(gatherId)).thenReturn(Optional.empty());

            // when & then
            BaseException exception = assertThrows(BaseException.class,
                    () -> memberService.approval(memberId, gatherId, nonManagerUser));

            // 예외가 BaseException이어야 하며, ExceptionEnum.MANAGER_NOT_FOUND을 포함해야 합니다.
            assertEquals(ExceptionEnum.MANAGER_NOT_FOUND, exception.getExceptionEnum());
            verify(memberRepository, never()).save(any());
        }

        @Nested
        @DisplayName("멤버 거절")
        class refusal{
            @Test
            @DisplayName("가입 거절완료")
            void success_refusal(){
                // given
                long memberId = member.getId();
                long gatherId = gather.getId();

                // 매니저 ID를 반환하도록 설정
                when(memberRepository.findManagerIdByGatherId(gatherId)).thenReturn(Optional.of(authenticatedUser.getUserId()));
                when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

                //when
                memberService.refusal(memberId, gatherId, authenticatedUser);

                // then
                assertEquals(Permission.REFUSAL, member.getPermission());
                verify(memberRepository).findById(memberId);
                verify(memberRepository).save(member);
            }

            @Test
            @DisplayName("가입 거절 실패-권한없음")
            void fail_refusal(){
                // given
                long memberId = member.getId();
                long gatherId = gather.getId();
                AuthenticatedUser nonManagerUser = new AuthenticatedUser(UUID.randomUUID(), "nonmanager@example.com", null);

                // memberRepository에서 findManagerIdByGatherId 호출 시 Optional.empty()를 반환하도록 설정
                when(memberRepository.findManagerIdByGatherId(gatherId)).thenReturn(Optional.empty());

                BaseException exception = assertThrows(BaseException.class,
                        () -> memberService.refusal(memberId, gatherId, nonManagerUser));

                // 예외가 BaseException이어야 하며, ExceptionEnum.MANAGER_NOT_FOUND을 포함해야 합니다.
                assertEquals(ExceptionEnum.MANAGER_NOT_FOUND, exception.getExceptionEnum());
                verify(memberRepository, never()).save(any());
            }
        }
    }

    @Nested
    @DisplayName("멤버 탈퇴")
    class withdrawal{
        @Test
        @DisplayName("탈퇴 완료")
        void success_withdrawal(){
            //given
            AuthenticatedUser user = new AuthenticatedUser(member.getUser().getId(), "test@example.com", null);  // member의 userId와 일치하도록 설정
            long memberId = member.getId();
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

            //when
            memberService.withdrawal(memberId, user);

            //then
            verify(memberRepository, times(1)).save(member);
            assertNotNull(member.getDeletedAt());
        }

        @Test
        @DisplayName("탈퇴 실패 - 존재하지 않는 멤버")
        void fail_withdrawal_un_exist_member(){
            AuthenticatedUser user = new AuthenticatedUser(member.getUser().getId(), "test@example.com", null);  // member의 userId와 일치하도록 설정
            long memberId = member.getId();

            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            BaseException exception = assertThrows(BaseException.class,
                    () -> memberService.withdrawal(memberId, user));

            // 예외가 BaseException이어야 하며, ExceptionEnum.MEMBER_NOT_FOUND을 포함해야 합니다.
            assertEquals(ExceptionEnum.MEMBER_NOT_FOUND, exception.getExceptionEnum());
            verify(memberRepository, never()).save(any());
        }

        @Test
        @DisplayName("탈퇴 실패 - 존재하지 않는 유저")
        void fail_withdrawal_un_exist_user(){
            // given
            AuthenticatedUser user = new AuthenticatedUser(UUID.randomUUID(), "test@example.com", null);  // authenticatedUser의 ID는 member.getUser().getId()와 다르게 설정
            long memberId = member.getId();

            // member.getUser().getId()와 일치하지 않는 userId를 설정하여 유저가 존재하지 않도록 설정
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

            // when & then
            BaseException exception = assertThrows(BaseException.class,
                    () -> memberService.withdrawal(memberId, user));

            assertEquals(ExceptionEnum.USER_NOT_FOUND, exception.getExceptionEnum());
            verify(memberRepository, never()).save(any());
        }

        @Test
        @DisplayName("탈퇴 실패 - 이미 탈퇴한 유저")
        void fail_withdrawal_already_deleted_user(){
            // given
            AuthenticatedUser user = new AuthenticatedUser(member.getUser().getId(), "test@example.com", null);  // authenticatedUser의 ID는 member.getUser().getId()와 다르게 설정
            long memberId = 1L;
            member.setDeletedAt(LocalDateTime.now());  // 삭제된 상태로 설정
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

            //when
            BaseException exception = assertThrows(BaseException.class,
                    () -> memberService.withdrawal(memberId, user));

            assertEquals(ExceptionEnum.ALREADY_DELETED_MEMBER, exception.getExceptionEnum());
            verify(memberRepository, never()).save(any());
        }
    }
}
