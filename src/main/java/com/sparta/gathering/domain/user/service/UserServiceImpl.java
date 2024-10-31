package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.user.dto.request.SignupRequest;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import com.sparta.gathering.domain.user.service.factory.UserFactory;
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

    @Value("${default.profile.image.url}")
    private String defaultProfileImageUrl;

    @Transactional
    @Override
    public User createUser(SignupRequest signupRequest) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new BaseException(ExceptionEnum.USER_ALREADY_EXISTS);
        }
        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        // User 객체 생성
        User user = UserFactory.of(
                signupRequest.getEmail(),
                signupRequest.getNickName(),
                encodedPassword,
                UserRole.ROLE_USER,  // 기본적으로 ROLE_USER로 설정
                signupRequest.getIdentityProvider(),  // 일반 로그인 사용자는 NONE
                defaultProfileImageUrl
        );
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(AuthenticatedUser authenticatedUser) {
        // 유저 조회
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        // 이미 삭제된 사용자일 경우 예외 발생
        if (user.getDeletedAt() != null) {
            throw new BaseException(ExceptionEnum.ALREADY_DELETED);
        }

        // 소프트 삭제 처리
        user.setDeletedAt();
        userRepository.save(user);
    }

    // 이메일로 사용자 조회
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }

    // 로그인 시 인증 처리
    @Override
    public User authenticateUser(String email, String rawPassword) {
        User user = findByEmail(email);
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BaseException(ExceptionEnum.EMAIL_PASSWORD_MISMATCH);
        }
        return user;
    }

}
