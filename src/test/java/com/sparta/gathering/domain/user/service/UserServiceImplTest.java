package com.sparta.gathering.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.agreement.entity.Agreement;
import com.sparta.gathering.domain.agreement.enums.AgreementType;
import com.sparta.gathering.domain.agreement.service.AgreementService;
import com.sparta.gathering.domain.emailverification.service.EmailVerificationService;
import com.sparta.gathering.domain.user.dto.request.SignupRequest;
import com.sparta.gathering.domain.user.dto.response.UserProfileResponse;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import com.sparta.gathering.domain.user.service.factory.UserFactory;
import com.sparta.gathering.domain.user.validation.UserValidation;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AgreementService agreementService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private UserValidation userValidation;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserServiceImpl userService;

    private final String defaultProfileImageUrl = "http://example.com/default-profile.png";

    // 회원가입 성공 테스트
    @Test
    @DisplayName("회원가입 성공 - 이메일 인증 완료 및 모든 조건 충족")
    void createUserSuccess() {
        // Given
        UUID privacyPolicyId = UUID.randomUUID();
        UUID termsOfServiceId = UUID.randomUUID();

        SignupRequest signupRequest = SignupRequest.builder()
                .email("test@example.com")
                .password("password123!A")
                .nickName("TestUser")
                .identityProvider(IdentityProvider.NONE)
                .agreedAgreementIds(List.of(privacyPolicyId, termsOfServiceId))
                .build();

        Agreement privacyPolicyAgreement = new Agreement(
                privacyPolicyId,
                "Privacy Policy Content",
                "v1.0",
                AgreementType.PRIVACY_POLICY
        );

        Agreement termsOfServiceAgreement = new Agreement(
                termsOfServiceId,
                "Terms of Service Content",
                "v1.0",
                AgreementType.TERMS_OF_SERVICE
        );

        // Mock 설정
        when(emailVerificationService.isVerified(signupRequest.getEmail())).thenReturn(true); // 이메일 인증 완료
        when(userRepository.findByEmail(signupRequest.getEmail())).thenReturn(Optional.empty()); // 가입된 이메일 없음
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword"); // 비밀번호 암호화
        when(agreementService.getLatestAgreement(AgreementType.PRIVACY_POLICY)).thenReturn(privacyPolicyAgreement);
        when(agreementService.getLatestAgreement(AgreementType.TERMS_OF_SERVICE)).thenReturn(termsOfServiceAgreement);
        when(agreementService.getAllLatestAgreements()).thenReturn(
                List.of(privacyPolicyAgreement, termsOfServiceAgreement));

        // When
        userService.createUser(signupRequest);

        // Then
        verify(userRepository, times(1)).save(any(User.class));
    }

    // 회원가입 실패 테스트 - 이메일 인증 미완료
    @Test
    @DisplayName("회원가입 실패 - 이메일 인증 미완료")
    void createUserFailureEmailNotVerified() {
        // Given
        SignupRequest signupRequest = SignupRequest.builder()
                .email("test@example.com")
                .password("password123!A")
                .nickName("TestUser")
                .identityProvider(IdentityProvider.NONE)
                .agreedAgreementIds(List.of(UUID.randomUUID(), UUID.randomUUID()))
                .build();

        when(emailVerificationService.isVerified(signupRequest.getEmail())).thenReturn(false);

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            userService.createUser(signupRequest);
        });

        assertEquals(ExceptionEnum.EMAIL_VERIFICATION_REQUIRED, exception.getExceptionEnum());
    }

    // 회원가입 실패 테스트 - 이미 존재하는 이메일
    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일")
    void createUserFailureEmailAlreadyExists() {
        // Given
        SignupRequest signupRequest = SignupRequest.builder()
                .email("test@example.com")
                .password("password123!A")
                .nickName("TestUser")
                .identityProvider(IdentityProvider.NONE)
                .agreedAgreementIds(List.of(UUID.randomUUID(), UUID.randomUUID()))
                .build();

        User existingUser = mock(User.class);

        when(emailVerificationService.isVerified(signupRequest.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(signupRequest.getEmail())).thenReturn(Optional.of(existingUser));

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            userService.createUser(signupRequest);
        });

        assertEquals(ExceptionEnum.USER_ALREADY_EXISTS, exception.getExceptionEnum());
    }

    // 회원가입 실패 테스트 - 필수 약관 미동의
    @Test
    @DisplayName("회원가입 실패 - 필수 약관 미동의")
    void createUserFailureRequiredAgreementsNotAccepted() {
        // Given
        UUID privacyPolicyId = UUID.randomUUID();

        SignupRequest signupRequest = SignupRequest.builder()
                .email("test@example.com")
                .password("password123!A")
                .nickName("TestUser")
                .identityProvider(IdentityProvider.NONE)
                .agreedAgreementIds(List.of(privacyPolicyId)) // 하나의 약관만 동의
                .build();

        Agreement privacyPolicyAgreement = new Agreement(
                privacyPolicyId,
                "Privacy Policy Content",
                "v1.0",
                AgreementType.PRIVACY_POLICY
        );

        Agreement termsOfServiceAgreement = new Agreement(
                UUID.randomUUID(),
                "Terms of Service Content",
                "v1.0",
                AgreementType.TERMS_OF_SERVICE
        );

        // Mock 설정
        when(emailVerificationService.isVerified(signupRequest.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(signupRequest.getEmail())).thenReturn(Optional.empty());
        when(agreementService.getLatestAgreement(AgreementType.PRIVACY_POLICY)).thenReturn(privacyPolicyAgreement);
        when(agreementService.getLatestAgreement(AgreementType.TERMS_OF_SERVICE)).thenReturn(termsOfServiceAgreement);

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            userService.createUser(signupRequest);
        });

        assertEquals(ExceptionEnum.AGREEMENT_NOT_ACCEPTED, exception.getExceptionEnum());
    }

    // 사용자 프로필 조회 성공 테스트
    @Test
    @DisplayName("사용자 프로필 조회 성공")
    void getUserProfileSuccess() {
        // Given
        UUID userId = UUID.randomUUID();

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId, "test@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        User user = UserFactory.of(
                "test@example.com",
                "TestUser",
                "encodedPassword",
                UserRole.ROLE_USER,
                IdentityProvider.NONE,
                defaultProfileImageUrl
        );

        when(userValidation.findUserById(userId)).thenReturn(user);

        // When
        UserProfileResponse response = userService.getUserProfile(authenticatedUser);

        // Then
        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getNickName(), response.getNickName());
        assertEquals(user.getProfileImage(), response.getProfileImage());
    }

    // 사용자 프로필 조회 실패 테스트 - 인증되지 않은 사용자
    @Test
    @DisplayName("사용자 프로필 조회 실패 - 인증되지 않은 사용자")
    void getUserProfileFailureUnauthenticatedUser() {
        // Given
        AuthenticatedUser unauthenticatedUser = null;

        doThrow(new BaseException(ExceptionEnum.UNAUTHORIZED_USER))
                .when(userValidation).validateAuthenticatedUser(unauthenticatedUser);

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            userService.getUserProfile(unauthenticatedUser);
        });

        assertEquals(ExceptionEnum.UNAUTHORIZED_USER, exception.getExceptionEnum());
    }

    // 비밀번호 변경 성공 테스트
    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePasswordSuccess() {
        // Given
        UUID userId = UUID.randomUUID();

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId, "test@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        User user = UserFactory.of(
                "test@example.com",
                "TestUser",
                "encodedOldPassword",
                UserRole.ROLE_USER,
                IdentityProvider.NONE,
                defaultProfileImageUrl
        );

        String oldPassword = "oldPassword123!";
        String newPassword = "newPassword456!";

        when(userValidation.findUserById(userId)).thenReturn(user);
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(newPassword, user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        // When
        userService.changePassword(authenticatedUser, oldPassword, newPassword);

        // Then
        verify(userRepository, times(1)).save(user);
        assertEquals("encodedNewPassword", user.getPassword());
    }

    // 비밀번호 변경 실패 테스트 - 현재 비밀번호 불일치
    @Test
    @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 불일치")
    void changePasswordFailureIncorrectOldPassword() {
        // Given
        UUID userId = UUID.randomUUID();

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId, "test@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        User user = UserFactory.of(
                "test@example.com",
                "TestUser",
                "encodedPassword",
                UserRole.ROLE_USER,
                IdentityProvider.NONE,
                defaultProfileImageUrl
        );

        String oldPassword = "wrongOldPassword";
        String newPassword = "newPassword456!";

        when(userValidation.findUserById(userId)).thenReturn(user);
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(false);

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            userService.changePassword(authenticatedUser, oldPassword, newPassword);
        });

        assertEquals(ExceptionEnum.PASSWORD_MISMATCH, exception.getExceptionEnum());
    }

    // 닉네임 변경 성공 테스트
    @Test
    @DisplayName("닉네임 변경 성공")
    void changeNickNameSuccess() {
        // Given
        UUID userId = UUID.randomUUID();

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId, "test@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        User user = UserFactory.of(
                "test@example.com",
                "OldNickName",
                "encodedPassword",
                UserRole.ROLE_USER,
                IdentityProvider.NONE,
                defaultProfileImageUrl
        );

        String newNickName = "NewNickName";

        when(userValidation.findUserById(userId)).thenReturn(user);

        // When
        userService.changeNickName(authenticatedUser, newNickName);

        // Then
        verify(userRepository, times(1)).save(user);
        assertEquals(newNickName, user.getNickName());
    }

    // 닉네임 변경 실패 테스트 - 새로운 닉네임이 기존과 동일
    @Test
    @DisplayName("닉네임 변경 실패 - 새로운 닉네임이 기존과 동일")
    void changeNickNameFailureSameAsOldNickName() {
        // Given
        UUID userId = UUID.randomUUID();

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId, "test@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        String sameNickName = "SameNickName";

        User user = UserFactory.of(
                "test@example.com",
                sameNickName,
                "encodedPassword",
                UserRole.ROLE_USER,
                IdentityProvider.NONE,
                defaultProfileImageUrl
        );

        when(userValidation.findUserById(userId)).thenReturn(user);

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            userService.changeNickName(authenticatedUser, sameNickName);
        });

        assertEquals(ExceptionEnum.NICKNAME_SAME_AS_OLD, exception.getExceptionEnum());
    }

    // 회원탈퇴 성공 테스트
    @Test
    @DisplayName("회원탈퇴 성공")
    void deleteUserSuccess() {
        // Given
        UUID userId = UUID.randomUUID();

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId, "test@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        User user = UserFactory.of(
                "test@example.com",
                "TestUser",
                "encodedPassword",
                UserRole.ROLE_USER,
                IdentityProvider.NONE,
                defaultProfileImageUrl
        );

        String refreshToken = "sampleRefreshToken";
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(userValidation.findUserById(userId)).thenReturn(user);

        // When
        userService.deleteUser(authenticatedUser, refreshToken, response);

        // Then
        verify(userRepository, times(1)).save(user);
        assertNotNull(user.getDeletedAt());
        verify(authService, times(1)).logout(refreshToken, response);
    }

    // 회원탈퇴 실패 테스트 - 이미 탈퇴한 사용자
    @Test
    @DisplayName("회원탈퇴 실패 - 이미 탈퇴한 사용자")
    void deleteUserFailureAlreadyDeletedUser() {
        // Given
        UUID userId = UUID.randomUUID();

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId, "test@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        User user = UserFactory.of(
                "test@example.com",
                "TestUser",
                "encodedPassword",
                UserRole.ROLE_USER,
                IdentityProvider.NONE,
                defaultProfileImageUrl
        );

        user.setDeletedAt(); // 이미 탈퇴한 사용자로 설정

        String refreshToken = "sampleRefreshToken";
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(userValidation.findUserById(userId)).thenReturn(user);
        doThrow(new BaseException(ExceptionEnum.ALREADY_DELETED))
                .when(userValidation).validateUserNotDeleted(user);

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            userService.deleteUser(authenticatedUser, refreshToken, response);
        });

        assertEquals(ExceptionEnum.ALREADY_DELETED, exception.getExceptionEnum());
    }

}
