package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.user.dto.request.SignupRequest;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new BaseException(ExceptionEnum.USER_ALREADY_EXISTS);
        }
        User user = User.createWithAutoUUID(
                signupRequest.getEmail(),
                signupRequest.getNickName(),
                signupRequest.getPassword(),
                UserRole.ROLE_USER,  // 기본적으로 ROLE_USER로 설정
                signupRequest.getIdentityProvider(),  // 일반 로그인 사용자는 NONE
                defaultProfileImageUrl
        );
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(String tokenUserId) {

        // 유저 조회
        User user = userRepository.findById(UUID.fromString(tokenUserId))
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        // 이미 삭제된 사용자일 경우 예외 발생
        if (user.getDeletedAt() != null) {
            throw new BaseException(ExceptionEnum.ALREADY_DELETED);
        }

        // 소프트 삭제 처리
        user.setDeletedAt();
        userRepository.save(user);

    }

    @Override
    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }

    @Override
    public User findByProviderIdAndIdentityProvider(String providerId,
            IdentityProvider identityProvider) {
        return userRepository.findByProviderIdAndIdentityProvider(providerId, identityProvider)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }

    public User authenticateUser(String email, String rawPassword) {
        User user = findByEmail(email);
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BaseException(ExceptionEnum.EMAIL_PASSWORD_MISMATCH);
        }
        return user;
    }

}
