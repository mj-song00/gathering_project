package com.sparta.gathering.domain.comment.commentServiceTest;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.comment.dto.request.CommentRequest;
import com.sparta.gathering.domain.comment.dto.response.CommentResponse;
import com.sparta.gathering.domain.comment.entity.Comment;
import com.sparta.gathering.domain.comment.repository.CommentRepository;
import com.sparta.gathering.domain.comment.service.CommentService;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import com.sparta.gathering.domain.schedule.entity.Schedule;
import com.sparta.gathering.domain.schedule.repository.ScheduleRepository;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private CommentService commentService;

    @Mock
    private Comment comment;   // Mocked Comment

    @Mock
    private Comment comment1;   // Mocked Comment

    @Mock
    private Comment comment2;   // Mocked Comment
    @Mock
    private Member mockMember; // Mocked Member
    @Mock
    private User mockUser;     // Mocked User

    private AuthenticatedUser authenticatedUser;
    private CommentRequest request;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        // Mocked User setup
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test1@test.com")
                .password("password123A!")
                .nickName("nickname1")
                .userRole(UserRole.ROLE_ADMIN)
                .identityProvider(IdentityProvider.NONE)
                .profileImage(null)
                .build();

        authenticatedUser = new AuthenticatedUser(user.getId(), user.getEmail(), null);
        request = new CommentRequest("댓글");
        schedule = new Schedule();
    }

    @Test
    public void userRepositoryFindByIdShouldReturnUser() {
        when(userRepository.findById(authenticatedUser.getUserId())).thenReturn(Optional.of(mockUser));
        Optional<User> foundUser = userRepository.findById(authenticatedUser.getUserId());
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get()).isEqualTo(mockUser);
    }

    @Test
    @DisplayName("isValidMember 메서드 확인")
    public void isValidMemberShouldReturnMember() {
        // Setup mock for memberRepository
        when(memberRepository.findByUserId(authenticatedUser.getUserId())).thenReturn(Optional.of(mockMember));

        Member result = commentService.isValidMember(authenticatedUser);
        assertThat(result).isEqualTo(mockMember);
        verify(memberRepository, times(1)).findByUserId(authenticatedUser.getUserId());
    }

    @Test
    @DisplayName("댓글 생성이 정상적으로 작동")
    public void createCommentShouldCreateAndSaveCommentSuccessfully() {
        // Arrange
        Long scheduleId = 1L; // Example schedule ID
        String commentText = "This is a test comment."; // Example comment text
        request = new CommentRequest(commentText); // Set up the request object

        // Mock user and member objects
        User mockUser = mock(User.class);
        when(mockUser.getNickName()).thenReturn("testNickName");

        Member mockMember = mock(Member.class);
        when(mockMember.getUser()).thenReturn(mockUser); // Ensure that mockMember returns mockUser

        // Mock the repository methods
        when(memberRepository.findByUserId(authenticatedUser.getUserId())).thenReturn(Optional.of(mockMember)); // Mock isValidMember
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule)); // Mock findSchedule
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Mock save to return the saved Comment

        // Act
        commentService.createComment(scheduleId, authenticatedUser, request);

        // Assert
        // Verify interactions
        verify(memberRepository, times(1)).findByUserId(authenticatedUser.getUserId());
        verify(scheduleRepository, times(1)).findById(scheduleId);
        verify(commentRepository, times(1)).save(argThat(comment ->
                comment.getComment().equals(commentText) &&
                        comment.getSchedule().equals(schedule) &&
                        comment.getMember().equals(mockMember)
        ));
    }

    @Test
    public void createCommentShouldThrowExceptionWhenUserIsNotMember() {
        when(memberRepository.findByUserId(any())).thenReturn(Optional.empty());
        assertThrows(BaseException.class, () -> commentService.createComment(1L, authenticatedUser, request));
    }

    @Test
    @DisplayName("업데이트가 정상적으로 작동 확인")
    public void updateCommentShouldUpdateCommentWhenUserIsAuthorized() {
        Long scheduleId = 1L;
        Long commentId = 1L;
        String updatedCommentText = "Updated comment text";
        request = new CommentRequest(updatedCommentText);

        // Setup mocks
        when(commentRepository.findByScheduleIdAndIdAndDeletedAtIsNull(scheduleId, commentId)).thenReturn(Optional.of(comment));
        when(comment.getMember()).thenReturn(mockMember);
        when(mockMember.getUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(authenticatedUser.getUserId());

        commentService.updateComment(request, scheduleId, commentId, authenticatedUser);

        verify(comment, times(1)).update(updatedCommentText);
        verify(commentRepository, times(1)).findByScheduleIdAndIdAndDeletedAtIsNull(scheduleId, commentId);
    }

    @Test
    @DisplayName("승인되지 않은 사용자 예외처리")
    public void updateCommentShouldThrowExceptionWhenUserIsNotAuthorized() {
        Long scheduleId = 1L;
        Long commentId = 1L;
        request = new CommentRequest("Unauthorized update attempt");

        // Setup mocks
        when(commentRepository.findByScheduleIdAndIdAndDeletedAtIsNull(scheduleId, commentId)).thenReturn(Optional.of(comment));
        when(comment.getMember()).thenReturn(mockMember);
        when(mockMember.getUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(UUID.randomUUID()); // Simulate unauthorized user

        BaseException exception = assertThrows(BaseException.class, () ->
                commentService.updateComment(request, scheduleId, commentId, authenticatedUser));

        assertThat(exception.getExceptionEnum()).isEqualTo(ExceptionEnum.UNAUTHORIZED_ACTION);
        verify(commentRepository, times(1)).findByScheduleIdAndIdAndDeletedAtIsNull(scheduleId, commentId);
        verify(comment, never()).update(anyString());
    }

    @Test
    public void getCommentShouldReturnCommentListWhenScheduleIdIsValid() {
        Long scheduleId = 1L;
        // Setup mocks
        when(comment1.getComment()).thenReturn("First comment");
        when(comment2.getComment()).thenReturn("Second comment");
        when(commentRepository.findAllByScheduleIdAndDeletedAtIsNullOrderByUpdatedAtDesc(scheduleId))
                .thenReturn(Arrays.asList(comment1, comment2));

        List<CommentResponse> result = commentService.getComment(scheduleId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getComment()).isEqualTo("First comment");
        assertThat(result.get(1).getComment()).isEqualTo("Second comment");
        verify(commentRepository, times(1)).findAllByScheduleIdAndDeletedAtIsNullOrderByUpdatedAtDesc(scheduleId);
    }

    @Test
    public void deleteCommentShouldDeleteCommentWhenUserIsAuthorized() {
        Long scheduleId = 1L;
        Long commentId = 1L;

        // Setup the mock behavior for the User and Member
        when(mockMember.getUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(authenticatedUser.getUserId());

        // Setup the mock repository methods
        when(commentRepository.findByScheduleIdAndIdAndDeletedAtIsNull(scheduleId, commentId))
                .thenReturn(Optional.of(comment));
        when(comment.getMember()).thenReturn(mockMember);

        // Ensure memberRepository returns the mock member when searching by user ID
        when(memberRepository.findByUserId(authenticatedUser.getUserId())).thenReturn(Optional.of(mockMember));

        // Act
        commentService.deleteComment(authenticatedUser, scheduleId, commentId);

        // Assert
        verify(comment, times(1)).delete(); // Verify delete method was called
        verify(commentRepository, times(1)).findByScheduleIdAndIdAndDeletedAtIsNull(scheduleId, commentId);
        verify(memberRepository, times(1)).findByUserId(authenticatedUser.getUserId()); // Ensure memberRepo was called
    }
}
