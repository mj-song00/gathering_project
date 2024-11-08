package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.agreement.entity.Agreement;
import com.sparta.gathering.domain.agreement.enums.AgreementStatus;
import com.sparta.gathering.domain.agreement.enums.AgreementType;
import com.sparta.gathering.domain.agreement.repository.AgreementRepository;
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
import com.sparta.gathering.domain.useragreement.repository.UserAgreementRepository;
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
    private final AgreementRepository agreementRepository;
    private final UserAgreementRepository userAgreementRepository;
    private final AgreementService agreementService;
    private final UserValidation userValidation;
    private final EmailVerificationService emailVerificationService;

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
                UserRole.ROLE_USER,  // 기본적으로 ROLE_USER로 설정
                signupRequest.getIdentityProvider(),  // 일반 로그인 사용자는 NONE
                defaultProfileImageUrl
        );

        // 약관 동의 상태 저장
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

        // 필수 약관에 대해 최신 버전 동의 여부 확인
        for (AgreementType type : requiredAgreements) {
            Agreement latestAgreement = agreementService.getLatestAgreement(type);
            if (!signupRequest.hasAgreedTo(latestAgreement.getId())) {
                throw new BaseException(ExceptionEnum.LATEST_AGREEMENT_NOT_FOUND);
            }
        }
    }

    // 약관 동의 상태 저장 메서드
    private void saveUserAgreements(User user, SignupRequest signupRequest) {
        // 최신 약관 리스트 가져오기
        List<Agreement> latestAgreements = agreementService.getAllLatestAgreements();

        // 요청된 약관 ID가 최신 약관 ID 리스트에 포함되는지 확인
        List<UUID> agreedIds = signupRequest.getAgreedAgreementIds();

        for (Agreement agreement : latestAgreements) {
            AgreementStatus status;

            // 필수 약관은 무조건 AGREE, 선택적 약관은 요청에 포함 여부에 따라 결정
            if (agreement.getType() == AgreementType.PRIVACY_POLICY
                    || agreement.getType() == AgreementType.TERMS_OF_SERVICE) {
                if (!agreedIds.contains(agreement.getId())) {
                    throw new BaseException(ExceptionEnum.AGREEMENT_NOT_ACCEPTED); // 필수 약관 동의가 없을 경우 예외 발생
                }
                status = AgreementStatus.AGREED;
            } else {
                status = agreedIds.contains(agreement.getId()) ? AgreementStatus.AGREED : AgreementStatus.DISAGREED;
            }

            // UserAgreement 객체 생성 후 사용자에 추가
            UserAgreement userAgreement = new UserAgreement(user, agreement, status);
            user.addUserAgreement(userAgreement);
            log.info("UserAgreement added: userId={}, agreementId={}, status={}", user.getId(), agreement.getId(),
                    status);
        }
    }


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

    @Transactional
    @Override
    public void deleteUser(AuthenticatedUser authenticatedUser) {

        // 인증된 사용자 확인
        userValidation.validateAuthenticatedUser(authenticatedUser);

        // 인증된 사용자의 ID로 사용자 조회
        User user = userValidation.findUserById(authenticatedUser.getUserId());

        // 사용자 탈퇴 여부 확인
        userValidation.validateUserNotDeleted(user);

        // 소프트 삭제 처리
        user.setDeletedAt();
        userRepository.save(user);
    }

}
