package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.agreement.entity.Agreement;
import com.sparta.gathering.domain.agreement.enums.AgreementStatus;
import com.sparta.gathering.domain.agreement.enums.AgreementType;
import com.sparta.gathering.domain.agreement.service.AgreementService;
import com.sparta.gathering.domain.emailverification.service.EmailVerificationService;
import com.sparta.gathering.domain.user.dto.request.SignupRequest;
import com.sparta.gathering.domain.user.dto.response.UserProfileResponse;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import com.sparta.gathering.domain.user.service.factory.UserFactory;
import com.sparta.gathering.domain.user.validation.UserValidation;
import com.sparta.gathering.domain.useragreement.entity.UserAgreement;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AgreementService agreementService;
    private final UserValidation userValidation;
    private final EmailVerificationService emailVerificationService;
    private final AuthService authService;

    @Value("${default.profile.image.url}")
    private String defaultProfileImageUrl;

    @Transactional
    @Override
    public void createUser(SignupRequest signupRequest) {

        // 이메일 인증 여부 확인
        if (!emailVerificationService.isVerified(signupRequest.getEmail())) {
            throw new BaseException(ExceptionEnum.EMAIL_VERIFICATION_REQUIRED);
        }

        // 이미 가입된 이메일인지 확인
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new BaseException(ExceptionEnum.USER_ALREADY_EXISTS);
        }

        // 필수 약관(PRIVACY_POLICY, TERMS_OF_SERVICE) 동의 여부 확인
        checkUserAgreements(signupRequest);

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        // 사용자 생성
        User user = UserFactory.of(
                signupRequest.getEmail(),
                signupRequest.getNickName(),
                encodedPassword,
                UserRole.ROLE_USER,
                signupRequest.getIdentityProvider(),
                defaultProfileImageUrl
        );

        // 동의 상태 저장 (약관 이력 포함)
        saveUserAgreements(user, signupRequest);

        userRepository.save(user);
    }

    // 필수 약관 동의 여부 확인 메서드
    private void checkUserAgreements(SignupRequest signupRequest) {
        // 필수 약관 리스트 설정
        List<AgreementType> requiredAgreements = List.of(
                AgreementType.PRIVACY_POLICY,
                AgreementType.TERMS_OF_SERVICE
        );

        // 필수 약관의 최신 동의 여부 확인
        for (AgreementType type : requiredAgreements) {
            Agreement latestAgreement = agreementService.getLatestAgreement(type);
            if (!signupRequest.hasAgreedTo(latestAgreement.getId())) {
                throw new BaseException(ExceptionEnum.AGREEMENT_NOT_ACCEPTED);
            }
        }
    }

    // 약관 동의 상태 저장 메서드
    private void saveUserAgreements(User user, SignupRequest signupRequest) {
        List<UUID> agreedIds = signupRequest.getAgreedAgreementIds();
        List<Agreement> latestAgreements = agreementService.getAllLatestAgreements();

        for (Agreement agreement : latestAgreements) {
            final AgreementStatus status = getStatus(agreement, agreedIds);

            // UserAgreement 객체 생성 후 사용자에 추가
            UserAgreement userAgreement = new UserAgreement(user, agreement, status);
            user.addUserAgreement(userAgreement);
        }
    }

    // 약관 동의 상태 확인 메서드
    private static AgreementStatus getStatus(Agreement agreement, List<UUID> agreedIds) {
        AgreementStatus status;

        if (agreement.getType() == AgreementType.PRIVACY_POLICY
                || agreement.getType() == AgreementType.TERMS_OF_SERVICE) {
            if (!agreedIds.contains(agreement.getId())) {
                throw new BaseException(ExceptionEnum.AGREEMENT_NOT_ACCEPTED);
            }
            status = AgreementStatus.AGREED;
        } else {
            status = agreedIds.contains(agreement.getId()) ? AgreementStatus.AGREED : AgreementStatus.DISAGREED;
        }
        return status;
    }

    // 사용자 프로필 조회
    @Transactional(readOnly = true)
    @Override
    public UserProfileResponse getUserProfile(AuthenticatedUser authenticatedUser) {

        // 인증된 사용자 확인
        userValidation.validateAuthenticatedUser(authenticatedUser);

        // 인증된 사용자의 ID로 사용자 조회
        User user = userValidation.findUserById(authenticatedUser.getUserId());

        // 사용자 탈퇴 여부 확인
        userValidation.validateUserNotDeleted(user);

        // 사용자 정보 반환
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .profileImage(user.getProfileImage())
                .build();

    }

    // 비밀번호 변경
    @Transactional
    @Override
    public void changePassword(AuthenticatedUser authenticatedUser, String oldPassword, String newPassword) {

        // 인증된 사용자 확인
        userValidation.validateAuthenticatedUser(authenticatedUser);

        // 인증된 사용자의 ID로 사용자 조회
        User user = userValidation.findUserById(authenticatedUser.getUserId());

        // 사용자 탈퇴 여부 확인
        userValidation.validateUserNotDeleted(user);

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BaseException(ExceptionEnum.PASSWORD_MISMATCH);
        }

        // 새 비밀번호가 기존 비밀번호와 동일한지 확인
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BaseException(ExceptionEnum.PASSWORD_SAME_AS_OLD);
        }

        // 새 비밀번호로 업데이트
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 닉네임 변경
    @Transactional
    @Override
    public void changeNickName(AuthenticatedUser authenticatedUser, String newNickName) {

        // 인증된 사용자 확인
        userValidation.validateAuthenticatedUser(authenticatedUser);

        // 인증된 사용자의 ID로 사용자 조회
        User user = userValidation.findUserById(authenticatedUser.getUserId());

        // 사용자 탈퇴 여부 확인
        userValidation.validateUserNotDeleted(user);

        // 새로운 닉네임이 기존 닉네임과 동일한지 확인
        if (user.getNickName().equals(newNickName)) {
            throw new BaseException(ExceptionEnum.NICKNAME_SAME_AS_OLD);
        }

        // 닉네임 업데이트
        user.setNickName(newNickName);
        userRepository.save(user);
    }

    // 회원탈퇴
    @Transactional
    @Override
    public void deleteUser(AuthenticatedUser authenticatedUser, String refreshToken, HttpServletResponse response) {

        // 인증된 사용자 확인
        userValidation.validateAuthenticatedUser(authenticatedUser);

        // 인증된 사용자의 ID로 사용자 조회
        User user = userValidation.findUserById(authenticatedUser.getUserId());

        // 사용자 탈퇴 여부 확인
        userValidation.validateUserNotDeleted(user);

        // 사용자 소프트 삭제 처리
        user.setDeletedAt();
        userRepository.save(user);

        // 로그아웃 처리 (리프레시 토큰 블랙리스트 및 쿠키 삭제)
        authService.logout(refreshToken, response);

    }

}
