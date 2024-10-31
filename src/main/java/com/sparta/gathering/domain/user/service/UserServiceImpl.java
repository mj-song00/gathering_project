package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.user.dto.request.SignupRequest;
import com.sparta.gathering.domain.user.dto.response.UserProfileResponse;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import com.sparta.gathering.domain.user.service.factory.UserFactory;
import com.sparta.gathering.domain.user.validation.UserValidation;
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
    private final UserValidation userValidation;

    @Value("${default.profile.image.url}")
    private String defaultProfileImageUrl;


    @Transactional
    @Override
    public void createUser(SignupRequest signupRequest) {

        // 이미 가입된 이메일인지 확인
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new BaseException(ExceptionEnum.USER_ALREADY_EXISTS);
        }

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
        userRepository.save(user);
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
