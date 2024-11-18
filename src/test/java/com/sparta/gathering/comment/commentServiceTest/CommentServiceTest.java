//package com.sparta.gathering.comment.commentServiceTest;
//
//
//import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
//import com.sparta.gathering.domain.comment.dto.request.CommentRequest;
//import com.sparta.gathering.domain.comment.entity.Comment;
//import com.sparta.gathering.domain.comment.repository.CommentRepository;
//import com.sparta.gathering.domain.comment.service.CommentService;
//import com.sparta.gathering.domain.member.entity.Member;
//import com.sparta.gathering.domain.member.repository.MemberRepository;
//import com.sparta.gathering.domain.schedule.entity.Schedule;
//import com.sparta.gathering.domain.schedule.repository.ScheduleRepository;
//import com.sparta.gathering.domain.user.entity.User;
//import com.sparta.gathering.domain.user.enums.IdentityProvider;
//import com.sparta.gathering.domain.user.enums.UserRole;
//import com.sparta.gathering.domain.user.service.factory.UserFactory;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//
//import java.util.Collections;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//
//
//@ExtendWith(MockitoExtension.class)
//public class CommentServiceTest {
//
//    @Mock
//    private CommentRepository commentRepository;
//
//    @Mock
//    private ScheduleRepository scheduleRepository;
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @InjectMocks
//    private CommentService commentService;
//
//    private AuthenticatedUser authenticatedUser;
//    private CommentRequest request;
//    private Schedule schedule;
//    private Member member;
//
//    @BeforeEach
//    public void setUp() {
//        User user = UserFactory.of(
//                "email@test.com",
//                "nickName",
//                "221331131311ff",
//                UserRole.ROLE_USER,  // 기본적으로 ROLE_USER로 설정
//                IdentityProvider.NONE,  // 일반 로그인 사용자는 NONE
//                "defaultProfileImageUrl"
//        );
//        UUID uuid = UUID.randomUUID();
//        authenticatedUser = new AuthenticatedUser(uuid, "testUser", Collections.singletonList(new SimpleGrantedAuthority(user.getUserRole().name())));
//        request = new CommentRequest("Test comment");
//        schedule = new Schedule();
//        member = new Member();
//    }
//
//    @Test
//    public void createComment_Successful() {
//        // Arrange: Set up the mocks and expected behavior
//        // Act: Call the createComment method
//        commentService.createComment(1L, authenticatedUser, request);
//
//        Comment comment = Comment.createComment(requestDto, user);
//        assertThat(request.getComment()).isEqualTo(comment.getName());
//        // Assert: Verify interactions and outcome
//        verify(commentRepository.save(any(Comment.class))); // Check that save was called with a Comment object
//    }
//
//}
